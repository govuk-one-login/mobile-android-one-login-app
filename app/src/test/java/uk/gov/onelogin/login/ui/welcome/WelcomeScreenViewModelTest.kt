package uk.gov.onelogin.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
import uk.gov.android.authentication.AuthenticationError
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.ui.LocaleUtils
import uk.gov.onelogin.ui.error.ErrorRoutes

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WelcomeScreenViewModelTest {
    private val mockContext: Context = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockVerifyIdToken: VerifyIdToken = mock()
    private val mockFeatureFlags: FeatureFlags = mock()
    private val mockGetPersistentId: GetPersistentId = mock()
    private val mockNavigator: Navigator = mock()
    private val mockOnlineChecker: OnlineChecker = mock()
    private val mockLocaleUtils: LocaleUtils = mock()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String? = "testIdToken"
    private val tokenResponse = TokenResponse(
        "testType",
        testAccessToken,
        1L,
        testIdToken,
        "testRefreshToken"
    )

    private val viewModel = WelcomeScreenViewModel(
        mockContext,
        mockLoginSession,
        mockCredChecker,
        mockBioPrefHandler,
        mockTokenRepository,
        mockAutoInitialiseSecureStore,
        mockVerifyIdToken,
        mockFeatureFlags,
        mockGetPersistentId,
        mockNavigator,
        mockLocaleUtils,
        mockOnlineChecker
    )

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null, device secure, no biometrics, verify id token success`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
            whenever(mockLoginSession.finalise(eq(mockIntent), any()))
                .thenAnswer {
                    (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent
            )

            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
            verify(mockAutoInitialiseSecureStore, times(1)).invoke()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device secure, no biometrics, id token is null`() =
        runTest {
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

            viewModel.handleActivityResult(mockIntent)

            verify(mockTokenRepository).setTokenResponse(nullIdTokenResponse)
            verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
            verify(mockAutoInitialiseSecureStore, times(1)).invoke()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() = runTest {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)

        viewModel.handleActivityResult(mockIntent)

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        verify(mockNavigator).navigate(LoginRoutes.BioOptIn, true)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `handleIntent when data != null and device is not secure`() = runTest {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)

        viewModel.handleActivityResult(mockIntent)

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.NONE)
        verify(mockNavigator).navigate(LoginRoutes.PasscodeInfo, true)
    }

    @Test
    fun `handleIntent when data == null`() = runTest {
        val mockIntent: Intent = mock()
        whenever(mockIntent.data).thenReturn(null)

        viewModel.handleActivityResult(mockIntent)

        verifyNoInteractions(mockTokenRepository)
        verifyNoInteractions(mockBioPrefHandler)
        verifyNoInteractions(mockNavigator)
    }

    @Test
    fun `When sign fails with AuthenticationError - it displays sign in error screen`() = runTest {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenThrow(AuthenticationError("Sign in error", AuthenticationError.ErrorType.OAUTH))

        viewModel.handleActivityResult(mockIntent)

        verifyNoInteractions(mockTokenRepository)
        verifyNoInteractions(mockBioPrefHandler)
        verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
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
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(false)

        viewModel.handleActivityResult(mockIntent)

        verifyNoInteractions(mockTokenRepository)
        verifyNoInteractions(mockBioPrefHandler)
        verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
    }

    @Test
    fun `check nav to dev panel calls navigator correctly`() {
        viewModel.navigateToDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }

    @Test
    fun `check nav to offline error calls navigator correctly`() {
        viewModel.navigateToOfflineError()

        verify(mockNavigator).navigate(ErrorRoutes.Offline, false)
    }
}