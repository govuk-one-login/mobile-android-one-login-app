package uk.gov.onelogin

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MainActivityViewModelTest {
    private val mockContext: Context = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockLifecycleOwner: LifecycleOwner = mock()
    private val mockNavigator: Navigator = mock()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String? = "testIdToken"
    private val tokenResponse = TokenResponse(
        "testType",
        testAccessToken,
        1L,
        testIdToken,
        "testRefreshToken"
    )

    private lateinit var viewModel: MainActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = MainActivityViewModel(
            mockBioPrefHandler,
            mockTokenRepository,
            mockNavigator,
            mockAutoInitialiseSecureStore
        )
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Test
    fun `secure store auto initialised`() {
        verify(mockAutoInitialiseSecureStore).invoke()
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
        verify(mockNavigator).navigate(LoginRoutes.Start)
    }
}
