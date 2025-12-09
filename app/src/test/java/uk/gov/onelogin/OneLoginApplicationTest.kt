package uk.gov.onelogin

import android.os.Looper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.ApplicationEntryPoint
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.criorchestrator.CheckIdCheckSessionState
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class OneLoginApplicationTest {
    private lateinit var app: OneLoginApplication

    private val entryPoint: ApplicationEntryPoint = mock()
    private val mockCheckIdCheckSessionState: CheckIdCheckSessionState = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockNavigator: Navigator = mock()
    private val mockLocalAuthManager: LocalAuthManager = mock()
    private val mockAnalyticsOptInRepo: AnalyticsOptInRepository = mock()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String = "testIdToken"
    private val tokenResponse = TokenResponse(
        "testType",
        testAccessToken,
        1L,
        testIdToken,
        "testRefreshToken"
    )

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext<OneLoginApplication>()

        // Setup mocks
        whenever(entryPoint.isIdCheckSessionActive()).thenReturn(mockCheckIdCheckSessionState)
        whenever(entryPoint.tokenRepository()).thenReturn(mockTokenRepository)
        whenever(entryPoint.navigator()).thenReturn(mockNavigator)
        whenever(entryPoint.localAuthManager()).thenReturn(mockLocalAuthManager)
        whenever(entryPoint.analyticsOptInRepo()).thenReturn(mockAnalyticsOptInRepo)

        app.appEntryPointProvider = { entryPoint }
    }

    @Test
    fun `onStop clears token and navigates when local auth is enabled and token is present`() {
        // Given
        whenever(mockLocalAuthManager.localAuthPreference).thenReturn(
            LocalAuthPreference.Enabled(
                true
            )
        )
        whenever(mockTokenRepository.getTokenResponse()).thenReturn(tokenResponse)
        whenever(mockCheckIdCheckSessionState.isIdCheckActive()).thenReturn(false)

        // When
        app.onStop(ProcessLifecycleOwner.get())

        // Then
        verify(mockTokenRepository).clearTokenResponse()
        verify(mockNavigator).navigate(LoginRoutes.Start)
    }

    @Test
    fun `onStop does nothing when local auth is disabled`() {
        whenever(mockLocalAuthManager.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)
        whenever(mockCheckIdCheckSessionState.isIdCheckActive()).thenReturn(false)

        app.onStop(ProcessLifecycleOwner.get())

        verify(mockTokenRepository, never()).clearTokenResponse()
        verify(mockNavigator, never()).navigate(any(), any())
    }

    @Test
    fun `onStop does nothing when token is null`() {
        whenever(mockLocalAuthManager.localAuthPreference).thenReturn(
            LocalAuthPreference.Enabled(
                true
            )
        )
        whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)
        whenever(mockCheckIdCheckSessionState.isIdCheckActive()).thenReturn(false)

        app.onStop(ProcessLifecycleOwner.get())

        verify(mockTokenRepository, never()).clearTokenResponse()
        verify(mockNavigator, never()).navigate(any(), any())
    }

    @Test
    fun `onStop does nothing when ID-Check journey is in progress`() {
        whenever(mockLocalAuthManager.localAuthPreference).thenReturn(
            LocalAuthPreference.Enabled(
                true
            )
        )
        whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)
        whenever(mockCheckIdCheckSessionState.isIdCheckActive()).thenReturn(true)

        app.onStop(ProcessLifecycleOwner.get())

        verify(mockTokenRepository, never()).clearTokenResponse()
        verify(mockNavigator, never()).navigate(any(), any())
    }

    @Test
    fun `AnalyticsRepo Synchronise is called from onStart`() = runTest {
        app.onStart(ProcessLifecycleOwner.get())
        shadowOf(Looper.getMainLooper()).idle()
        verify(mockAnalyticsOptInRepo).synchronise()
    }
}
