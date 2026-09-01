package uk.gov.onelogin.features.unit.login.domain.signin.remotelogin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.counter.CounterImpl
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.remove.RemoveRefreshTokenAndExpiry
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.TestActivityResultLauncher
import uk.gov.onelogin.core.utils.convertToLoginTokens
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.TestStartRemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("LargeClass")
@ExtendWith(CoroutinesTestExtension::class)
class RemoteLoginImplTest {
    private val mockContext: Context = mock<Context>().apply {
        whenever(getString(any(), any())).thenReturn("testUrl")
        whenever(getString(any())).thenReturn("test")
    }
    private val mockFragmentActivity: FragmentActivity = mock()
    private val localAuthManager: LocalAuthManager = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockVerifyIdToken: VerifyIdToken = mock()
    private val mockSaveTokenExpiry: SaveTokenExpiry = mock()
    private val startRemoteLogin = TestStartRemoteLogin()
    private val mockFinaliseRemoteLogin: FinaliseRemoteLogin = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val mockSavePersistentId: SavePersistentId = mock()
    private val recoverableErrors: Counter = CounterImpl()
    private val mockRemoveRefreshTokenAndExpiry: RemoveRefreshTokenAndExpiry = mock()
    private val logger = MemorisedLogger()
    private val mockIntent: Intent = mock()
    private val activityResultLauncher = TestActivityResultLauncher<Intent>()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String = "testIdToken"
    private val tokenResponse =
        TokenResponse(
            "testType",
            testAccessToken,
            1L,
            testIdToken,
            null
        )
    private val validRefreshToken =
        "ewogICJhbGciOiAiRVMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkI" +
            "jogImFiY2QtMTIzNCIKfQ.ewogICJhdWQiOiAidGVzdCIsCiAgImV4cCI6IDE3NjMxMDg2MTcKfQ.abdcd"
    private val tokenResponseWithRefresh =
        tokenResponse.copy(
            refreshToken = validRefreshToken
        )
    private val accessDeniedError =
        AuthenticationError(
            "access_denied",
            AuthenticationError.ErrorType.ACCESS_DENIED
        )
    private val oauthError =
        AuthenticationError(
            "oauth_error",
            AuthenticationError.ErrorType.OAUTH
        )
    private val oauthError500 =
        AuthenticationError(
            "oauth_error",
            AuthenticationError.ErrorType.OAUTH,
            status = 500
        )
    private val serverError =
        AuthenticationError(
            "server_error",
            AuthenticationError.ErrorType.SERVER_ERROR
        )
    private val tokenError400 =
        AuthenticationError(
            "token_error",
            AuthenticationError.ErrorType.TOKEN_ERROR
        )

    private val remoteLogin: RemoteLogin =
        RemoteLoginImpl(
            mockContext,
            mockFinaliseRemoteLogin,
            startRemoteLogin,
            localAuthManager,
            mockTokenRepository,
            mockVerifyIdToken,
            mockAutoInitialiseSecureStore,
            mockSavePersistentId,
            mockSaveTokenExpiry,
            mockSignOutUseCase,
            mockRemoveRefreshTokenAndExpiry,
            recoverableErrors,
            logger,
        )

    @BeforeEach
    fun setUp() = runTest {
        // Default happy path
        givenFinaliseRemoteLoginSuccess()
        givenVerifyIdTokenSuccess()
    }

    @Test
    fun `given no errors, start returns success`() = runTest {
        val result = remoteLogin.start(activityResultLauncher)

        assertEquals(RemoteLogin.Result.Success, result)
    }

    @Test
    fun `given ClientAttestationException, start returns app integrity failure`() = runTest {
        givenStartRemoteLoginFailure(
            AppIntegrityException.ClientAttestationException(Exception())
        )

        val result = remoteLogin.start(activityResultLauncher)

        assertEquals(
            RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
            result,
        )
    }

    @Test
    fun `given FirebaseException, start returns app integrity failure`() = runTest {
        givenStartRemoteLoginFailure(
            AppIntegrityException.FirebaseException(Exception())
        )

        val result = remoteLogin.start(activityResultLauncher)

        assertEquals(
            RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
            result,
        )
    }

    @Test
    fun `given AppIntegrityException Other, start returns app integrity failure`() = runTest {
        givenStartRemoteLoginFailure(
            AppIntegrityException.Other(Exception())
        )

        val result = remoteLogin.start(activityResultLauncher)

        assertEquals(
            RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
            result,
        )
    }

    @Test
    fun `given generic throwable, start returns recoverable failure`() = runTest {
        givenStartRemoteLoginFailure(RuntimeException("something went wrong"))

        val result = remoteLogin.start(activityResultLauncher)

        assertEquals(
            RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable),
            result,
        )
    }

    @Test
    fun `given passcode enabled, finalise saves tokens & returns success`() =
        runTest {
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(false))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            assertSecureStoreIsInitialised()
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given biometrics enabled, finalise saves tokens & returns success`() =
        runTest {
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(true))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            assertSecureStoreIsInitialised()
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given local auth disabled, finalise saves tokens & returns success`() =
        runTest {
            givenLocalAuthCheckSuccess(LocalAuthPreference.Disabled)

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            assertSecureStoreNotInitialised()
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given access denied error, finalise signs out & returns access denied failure`() =
        runTest {
            givenFinaliseRemoteLoginError(accessDeniedError)

            val result = remoteLogin.finalise(
                mockIntent,
                activity = mockFragmentActivity,
            )

            assertTokensNotSaved()
            assertSignedOut()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.AccessDenied),
                result,
            )
            assertThat("logger has log", logger.contains("access_denied"))
        }

    @Test
    fun `given oauth error, finalise returns unrecoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(oauthError)

            val result = remoteLogin.finalise(
                mockIntent,
                activity = mockFragmentActivity,
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable),
                result,
            )
            assertThat("logger has oauth_error", logger.contains("oauth_error"))
        }

    @Test
    fun `given oauth error 500, finalise returns unrecoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(oauthError500)

            val result = remoteLogin.finalise(
                mockIntent,
                activity = mockFragmentActivity,
            )

            assertTokensNotSaved()
            verifyNoInteractions(mockSignOutUseCase)
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable),
                result,
            )
            assertThat("logger has oauth_error", logger.contains("oauth_error"))
        }

    @Test
    fun `given server error & attempt 2, finalise returns recoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(serverError)
            givenErrorCount(1) // attempt 2

            val result = remoteLogin.finalise(
                mockIntent,
                activity = mockFragmentActivity,
            )

            assertTokensNotSaved()
            verifyNoInteractions(mockSignOutUseCase)
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable),
                result,
            )
            assertThat("logger has no server_error", logger.contains("server_error"))
        }

    @Test
    fun `given server error & attempt 3, finalise returns unrecoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(serverError)
            givenErrorCount(2) // attempt 3

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensNotSaved()
            verifyNoInteractions(mockSignOutUseCase)
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable),
                result,
            )
            assertThat("logger has server_error", logger.contains("server_error"))
        }

    @Test
    fun `given token error, finalise returns unrecoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(tokenError400)

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensNotSaved()
            verifyNoInteractions(mockSignOutUseCase)
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable),
                result,
            )
            assertThat("logger has token_error", logger.contains("token_error"))
        }

    @Test
    fun `given generic throwable, finalise returns recoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(Throwable())

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable),
                result,
            )
            assertThat("logger has log", logger.size == 1)
        }

    @Test
    fun `given null error, finalise returns recoverable failure`() =
        runTest {
            givenFinaliseRemoteLoginError(null)

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable),
                result,
            )
        }

    @Test
    fun `given ClientAttestationException, finalise returns app integrity failure`() =
        runTest {
            givenFinaliseRemoteLoginError(
                AppIntegrityException.ClientAttestationException(Exception())
            )

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
                result,
            )
        }

    @Test
    fun `given FirebaseException, finalise returns app integrity failure`() =
        runTest {
            givenFinaliseRemoteLoginError(
                AppIntegrityException.FirebaseException(Exception())
            )

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
                result,
            )
        }

    @Test
    fun `given AppIntegrityException Other, finalise returns app integrity failure`() =
        runTest {
            givenFinaliseRemoteLoginError(
                AppIntegrityException.Other(Exception())
            )

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity),
                result,
            )
        }

    @Test
    fun `given id token verification fails, finalise returns recoverable failure`() =
        runTest {
            givenVerifyIdTokenFailure()

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertSecureStoreNotInitialised()
            assertTokensNotSaved()
            assertEquals(
                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable),
                result,
            )
        }

    @Test
    fun `given local auth check fails, finalise returns success`() =
        runTest {
            givenLocalAuthCheckFailure()

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            assertSecureStoreNotInitialised()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given refresh token & passcode enabled, finalise saves refresh expiry & initialises secure store`() =
        runTest {
            givenFinaliseRemoteLoginSuccess(tokenResponseWithRefresh)
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(false))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved(withRefresh = true)
            assertSecureStoreIsInitialised(validRefreshToken)
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given refresh token & biometrics enabled, finalise saves refresh expiry & initialises secure store`() =
        runTest {
            givenFinaliseRemoteLoginSuccess(tokenResponseWithRefresh)
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(true))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved(withRefresh = true)
            assertSecureStoreIsInitialised(validRefreshToken)
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given refresh token & local auth disabled, finalise does not save refresh token or expiry`() =
        runTest {
            givenFinaliseRemoteLoginSuccess(tokenResponseWithRefresh)
            givenLocalAuthCheckSuccess(LocalAuthPreference.Disabled)

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            assertSecureStoreNotInitialised()
            assertErrorCountReset()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given refresh token & local auth enabled, finalise saves new refresh token`() =
        runTest {
            givenFinaliseRemoteLoginSuccess(tokenResponseWithRefresh)
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(false))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            verify(mockRemoveRefreshTokenAndExpiry, times(0)).remove()
            assertTokensSaved(withRefresh = true)
            assertSecureStoreIsInitialised(validRefreshToken)
            assertEquals(RemoteLogin.Result.Success, result)
        }

    @Test
    fun `given no refresh token & local auth enabled, finalise removes stored refresh token`() =
        runTest {
            givenLocalAuthCheckSuccess(LocalAuthPreference.Enabled(false))

            val result = remoteLogin.finalise(mockIntent, activity = mockFragmentActivity)

            assertTokensSaved()
            verify(mockRemoveRefreshTokenAndExpiry, times(1)).remove()
            assertSecureStoreIsInitialised()
            assertEquals(RemoteLogin.Result.Success, result)
        }

    private fun givenStartRemoteLoginFailure(throwable: Throwable) {
        startRemoteLogin.result = Result.failure(throwable)
    }

    private suspend fun givenLocalAuthCheckSuccess(pref: LocalAuthPreference) {
        whenever(localAuthManager.localAuthPreference).thenReturn(pref)
        whenever(localAuthManager.enforceAndSet(any(), any(), any())).thenAnswer {
            (it.arguments[2] as LocalAuthManagerCallbackHandler).onSuccess(false)
        }
    }

    private suspend fun givenLocalAuthCheckFailure() {
        whenever(localAuthManager.enforceAndSet(any(), any(), any())).thenAnswer {
            (it.arguments[2] as LocalAuthManagerCallbackHandler).onFailure(false)
        }
    }

    private suspend fun givenFinaliseRemoteLoginSuccess(response: TokenResponse = tokenResponse) {
        whenever(mockFinaliseRemoteLogin.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                it.getArgument<(TokenResponse) -> Unit>(2).invoke(response)
            }
    }

    private suspend fun givenFinaliseRemoteLoginError(error: Throwable?) {
        whenever(mockFinaliseRemoteLogin.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                it.getArgument<(Throwable?) -> Unit>(1).invoke(error)
            }
    }

    private suspend fun givenVerifyIdTokenSuccess() {
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)
    }

    private suspend fun givenVerifyIdTokenFailure() {
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(false)
    }

    private fun givenErrorCount(errorCount: Int) {
        recoverableErrors.reset()
        repeat(errorCount) {
            recoverableErrors.increment()
        }
    }

    private suspend fun assertTokensSaved(withRefresh: Boolean = false) {
        val response = if (withRefresh) tokenResponseWithRefresh else tokenResponse
        verify(mockTokenRepository).setTokenResponse(response.convertToLoginTokens())
        verify(mockSavePersistentId).invoke()
        verify(mockSaveTokenExpiry).saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = response.accessTokenExpirationTime
            )
        )
        if (withRefresh) {
            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = mockSaveTokenExpiry.extractExpFromRefreshToken(validRefreshToken)
                )
            )
        } else {
            verify(mockSaveTokenExpiry, times(0)).saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = any()
                )
            )
        }
    }

    private fun assertTokensNotSaved() {
        verifyNoInteractions(mockSavePersistentId)
        verifyNoInteractions(mockTokenRepository)
        verifyNoInteractions(mockSaveTokenExpiry)
    }

    private suspend fun assertSecureStoreIsInitialised(refreshToken: String? = null) {
        verify(mockAutoInitialiseSecureStore).initialise(refreshToken)
    }

    private fun assertSecureStoreNotInitialised() {
        verifyNoInteractions(mockAutoInitialiseSecureStore)
    }

    private suspend fun assertSignedOut() {
        verify(mockSignOutUseCase).invoke()
    }

    private fun assertErrorCountReset() {
        assertEquals(0, recoverableErrors.getValue())
    }
}
