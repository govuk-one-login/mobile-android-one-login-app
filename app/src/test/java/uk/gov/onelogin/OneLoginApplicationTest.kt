package uk.gov.onelogin

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.ApplicationEntryPoint
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository

@RunWith(AndroidJUnit4::class)
class OneLoginApplicationTest {
    private lateinit var app: OneLoginApplication

    private val entryPoint: ApplicationEntryPoint = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockNavigator: Navigator = mock()
    private val mockLocalAuthManager: LocalAuthManager = mock()

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
        whenever(entryPoint.tokenRepository()).thenReturn(mockTokenRepository)
        whenever(entryPoint.navigator()).thenReturn(mockNavigator)
        whenever(entryPoint.localAuthManager()).thenReturn(mockLocalAuthManager)

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

        // When
        app.onStop(ProcessLifecycleOwner.get())

        // Then
        verify(mockTokenRepository).clearTokenResponse()
        verify(mockNavigator).navigate(LoginRoutes.Start)
    }

    @Test
    fun `onStop does nothing when local auth is disabled`() {
        whenever(mockLocalAuthManager.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

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

        app.onStop(ProcessLifecycleOwner.get())

        verify(mockTokenRepository, never()).clearTokenResponse()
        verify(mockNavigator, never()).navigate(any(), any())
    }
}
