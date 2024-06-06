package uk.gov.onelogin

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.AuthenticationError
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.mainnav.nav.MainNavRoutes
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MainActivityViewModelTest {
    private val mockContext: Context = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockLifecycleOwner: LifecycleOwner = mock()
    private val mockVerifyIdToken: VerifyIdToken = mock()

    private val observer: Observer<String> = mock()
    private val testAccessToken = "testAccessToken"
    private var testIdToken: String? = "testIdToken"
    private val tokenResponse = TokenResponse(
        "testType",
        testAccessToken,
        1L,
        testIdToken,
        "testRefreshToken"
    )

    private val viewModel = MainActivityViewModel(
        mockContext,
        mockLoginSession,
        mockCredChecker,
        mockBioPrefHandler,
        mockTokenRepository,
        mockAutoInitialiseSecureStore,
        mockVerifyIdToken
    )

    @BeforeEach
    fun setup() {
        viewModel.next.observeForever(observer)
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Test
    fun `secure store auto initialised`() {
        verify(mockAutoInitialiseSecureStore).invoke()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null, device secure, no biometrics, verify id token success`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl"), any()))
            .thenAnswer {
                (it.arguments[2] as (verified: Boolean) -> Unit).invoke(true)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
        verify(mockAutoInitialiseSecureStore, times(2)).invoke()
        assertEquals(MainNavRoutes.START, viewModel.next.value)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device secure, no biometrics, id token is null`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()
        val nullIdTokenResponse = TokenResponse(
            tokenType = "testType",
            accessToken = testAccessToken,
            accessTokenExpirationTime = 1L,
            refreshToken = "testRefreshToken"
        )

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(nullIdTokenResponse)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(nullIdTokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
        verify(mockAutoInitialiseSecureStore, times(2)).invoke()
        assertEquals(MainNavRoutes.START, viewModel.next.value)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl"), any()))
            .thenAnswer {
                (it.arguments[2] as (verified: Boolean) -> Unit).invoke(true)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.BIO_OPT_IN, viewModel.next.value)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device is not secure`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl"), any()))
            .thenAnswer {
                (it.arguments[2] as (verified: Boolean) -> Unit).invoke(true)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.NONE)
        assertEquals(LoginRoutes.PASSCODE_INFO, viewModel.next.value)
    }

    @Test
    fun `handleIntent when data == null`() {
        val mockIntent: Intent = mock()
        whenever(mockIntent.data).thenReturn(null)

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository, times(0)).setTokenResponse(any())
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertNull(viewModel.next.value)
    }

    @Test
    fun `lock screen activates when app backgrounds`() {
        // GIVEN we are logged in
        whenever(mockTokenRepository.getTokenResponse()).thenReturn(tokenResponse)
        // AND user Biometric enabled
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.BIOMETRICS)

        // WHEN app goes in the background
        viewModel.onPause(owner = mockLifecycleOwner)

        // THEN token is removed from runtime memory
        verify(mockTokenRepository).clearTokenResponse()
        // AND user navigates to the lock screen (splash screen)
        verify(observer).onChanged(LoginRoutes.START)
    }

    @Test
    fun `When sign fails with AuthenticationError - it displays sign in error screen`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenThrow(AuthenticationError("Sign in error", AuthenticationError.ErrorType.OAUTH))
        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository, times(0)).setTokenResponse(any())
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.SIGN_IN_ERROR, viewModel.next.value)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `When id token verification fails - displays sign in error screen`() = runTest {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl"), any()))
            .thenAnswer {
                (it.arguments[2] as (verified: Boolean) -> Unit).invoke(false)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository, times(0)).setTokenResponse(any())
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.SIGN_IN_ERROR, viewModel.next.value)
    }
}
