package uk.gov.onelogin

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.ui.home.HomeRoutes

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MainActivityViewModelTest {
    private val mockAppRoutes: IAppRoutes = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockGetTokenExpiry: GetTokenExpiry = mock()
    private val mockGetFromSecureStore: GetFromSecureStore = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()

    private val observer: Observer<String> = mock()
    private val testAccessToken = "testAccessToken"
    private val tokenResponse = TokenResponse(
        "testType",
        testAccessToken,
        1L,
        "testIdToken",
        "testRefreshToken"
    )

    private val viewModel = MainActivityViewModel(
        mockAppRoutes,
        mockLoginSession,
        mockCredChecker,
        mockBioPrefHandler,
        mockGetTokenExpiry,
        mockGetFromSecureStore,
        mockTokenRepository,
        mockAutoInitialiseSecureStore
    )

    @BeforeEach
    fun setup() {
        viewModel.next.observeForever(observer)
    }

    @Test
    fun `secure store auto initialised`() {
        verify(mockAutoInitialiseSecureStore).invoke()
    }

    @Test
    fun `handleIntent when data != null and device is secure with no biometrics`() {
        val mockIntent: Intent = mock()
        val mockFragmentActivity: FragmentActivity = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)

        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

        viewModel.handleIntent(
            mockIntent,
            mockFragmentActivity

        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(HomeRoutes.START, viewModel.next.value)
    }

    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() {
        val mockIntent: Intent = mock()
        val mockFragmentActivity: FragmentActivity = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)

        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

        viewModel.handleIntent(
            mockIntent,
            mockFragmentActivity
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.BIO_OPT_IN, viewModel.next.value)
    }

    @Test
    fun `handleIntent when data != null and device is not secure`() {
        val mockIntent: Intent = mock()
        val mockFragmentActivity: FragmentActivity = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)

        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

        viewModel.handleIntent(
            mockIntent,
            mockFragmentActivity
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.NONE)
        assertEquals(LoginRoutes.PASSCODE_INFO, viewModel.next.value)
    }

    @Test
    fun `access token not expired and tokens exist`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockFragmentActivity: FragmentActivity = mock()
            whenever(mockIntent.data).thenReturn(null)
            whenever(mockBioPrefHandler.getBioPref()).thenReturn(null)
            whenever(mockGetTokenExpiry.invoke())
                .thenReturn(System.currentTimeMillis() + 10_000)
            whenever(
                mockGetFromSecureStore.invoke(mockFragmentActivity, Keys.ACCESS_TOKENS_KEY)
            ).thenReturn(testAccessToken)

            viewModel.handleIntent(
                mockIntent,
                mockFragmentActivity
            )

            verify(mockBioPrefHandler, times(0)).setBioPref(any())
            assertEquals(HomeRoutes.START, viewModel.next.value)
        }

    @Test
    fun `access token expired and tokens exist`() = runTest {
        val mockIntent: Intent = mock()
        val mockFragmentActivity: FragmentActivity = mock()

        whenever(mockIntent.data).thenReturn(null)
        val expiredTokenTimestamp = 100L
        whenever(mockGetTokenExpiry.invoke()).thenReturn(expiredTokenTimestamp)
        whenever(mockGetFromSecureStore.invoke(mockFragmentActivity, Keys.ACCESS_TOKENS_KEY))
            .thenReturn(testAccessToken)

        viewModel.handleIntent(
            mockIntent,
            mockFragmentActivity
        )

        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.START, viewModel.next.value)
    }

    @Test
    fun `access token not expired and tokens don't exist`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockFragmentActivity: FragmentActivity = mock()

            whenever(mockIntent.data).thenReturn(null)
            whenever(mockGetTokenExpiry.invoke())
                .thenReturn(System.currentTimeMillis() + 10_000)
            whenever(
                mockGetFromSecureStore.invoke(
                    mockFragmentActivity,
                    Keys.ACCESS_TOKENS_KEY
                )
            ).thenReturn(null)

            viewModel.handleIntent(
                mockIntent,
                mockFragmentActivity
            )

            verify(mockBioPrefHandler, times(0)).setBioPref(any())
            assertEquals(LoginRoutes.START, viewModel.next.value)
        }
}
