package uk.gov.onelogin

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Observer
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
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.ui.home.HomeRoutes

@ExtendWith(InstantExecutorExtension::class)
class MainActivityViewModelTest {
    private val mockAppRoutes: IAppRoutes = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()

    private val observer: Observer<String> = mock()

    private val tokenResponse = TokenResponse(
        "testType",
        "testAccessToken",
        1L,
        "testIdToken",
        "testRefreshToken"
    )

    private val viewModel = MainActivityViewModel(
        mockAppRoutes,
        mockLoginSession,
        mockCredChecker,
        mockBioPrefHandler,
        mockTokenRepository
    )

    @BeforeEach
    fun setup() {
        viewModel.next.observeForever(observer)
    }

    @Test
    fun `handleIntent when data != null and device is secure with no biometrics`() {
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)

        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

        viewModel.handleIntent(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(HomeRoutes.START, viewModel.next.value)
    }

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

        viewModel.handleIntent(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.BIO_OPT_IN, viewModel.next.value)
    }

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

        viewModel.handleIntent(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.NONE)
        assertEquals(LoginRoutes.PASSCODE_INFO, viewModel.next.value)
    }

    @Test
    fun `handleIntent when data == null and tokens available`() {
        val mockIntent: Intent = mock()

        whenever(mockIntent.data).thenReturn(null)

        whenever(
            mockTokenRepository.getTokenResponse()
        ).thenReturn(tokenResponse)

        viewModel.handleIntent(
            mockIntent
        )

        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(HomeRoutes.START, viewModel.next.value)
    }

    @Test
    fun `handleIntent when data == null and tokens not available`() {
        val mockIntent: Intent = mock()

        whenever(mockIntent.data).thenReturn(null)

        whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)

        viewModel.handleIntent(
            mockIntent
        )

        verify(mockBioPrefHandler, times(0)).setBioPref(any())
        assertEquals(LoginRoutes.START, viewModel.next.value)
    }
}
