package uk.gov.onelogin

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.mainnav.nav.MainNavRoutes
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MainActivityViewModelTest {
    private val mockLoginSession: LoginSession = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockLifecycleOwner: LifecycleOwner = mock()

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
        mockLoginSession,
        mockCredChecker,
        mockBioPrefHandler,
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
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)

        whenever(mockLoginSession.finalise(eq(mockIntent), any()))
            .thenAnswer {
                (it.arguments[1] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

        viewModel.handleActivityResult(
            mockIntent
        )

        verify(mockTokenRepository).setTokenResponse(tokenResponse)
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
        verify(mockAutoInitialiseSecureStore, times(2)).invoke()
        assertEquals(MainNavRoutes.START, viewModel.next.value)
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

        viewModel.handleActivityResult(
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
}
