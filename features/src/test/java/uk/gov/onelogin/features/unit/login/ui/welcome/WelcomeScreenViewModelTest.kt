package uk.gov.onelogin.features.unit.login.ui.welcome

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel

@Suppress("UNCHECKED_CAST", "LargeClass")
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WelcomeScreenViewModelTest {
    private lateinit var mockFragmentActivity: FragmentActivity
    private lateinit var mockActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var mockNavigator: Navigator
    private lateinit var mockOnlineChecker: OnlineChecker
    private lateinit var mockRemoteLogin: RemoteLogin

    private lateinit var viewModel: WelcomeScreenViewModel

    @BeforeEach
    fun setup() {
        mockFragmentActivity = mock()
        mockActivityResultLauncher = mock()
        analyticsLogger = mock()
        mockNavigator = mock()
        mockRemoteLogin = mock()
        mockOnlineChecker = mock()

        viewModel = WelcomeScreenViewModel(mockNavigator)
    }

    @Test
    fun `check nav to dev panel calls navigator correctly`() {
        viewModel.navigateToDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }
}
