package uk.gov.onelogin.features.unit.login.ui.welcome

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("UNCHECKED_CAST", "LargeClass")
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WelcomeScreenViewModelTest {
    private lateinit var mockFragmentActivity: FragmentActivity
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var mockNavigator: Navigator
    private lateinit var mockOnlineChecker: OnlineChecker
    private lateinit var mockRemoteLogin: RemoteLogin

    private lateinit var viewModel: WelcomeScreenViewModel

    @Test
    fun `verify on primary`() =
        runTest {
            viewModel.onPrimary(any())

            assertTrue(viewModel.loading.value)
            verify(mockRemoteLogin).start(any())
        }

    @Test
    fun `handle result when intent data is null`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            whenever(mockIntent.data).thenReturn(null)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            assertFalse(viewModel.loading.value)
            verify(mockRemoteLogin, times(0))
                .finalise(mockIntent, any(), any())
        }

    @Test
    fun `handle result when intent data is populated`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()
            whenever(mockIntent.data).thenReturn(mockUri)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            assertTrue(viewModel.loading.value)
            verify(mockRemoteLogin)
                .finalise(mockIntent, any(), any())
        }

    @Test
    fun `check nav to dev panel calls navigator correctly`() {
        createMocks()

        viewModel.navigateToDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }

    @Test
    fun `check nav to offline error calls navigator correctly`() {
        createMocks()

        viewModel.navigateToOfflineError()

        verify(mockNavigator).navigate(ErrorRoutes.Offline, false)
    }

    @Test
    fun `check abort login works as expected`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()

            whenever(mockRemoteLogin.finalise(eq(mockIntent), any(), any()))
                .thenAnswer {
                    runBlocking {
                        delay(10000)
                        assert(viewModel.loading.value)
                        viewModel.abortLogin(any())
                    }
                }

            assertFalse(viewModel.loading.value)
        }

    private fun createMocks() {
        analyticsLogger = mock()
        mockNavigator = mock()
        mockRemoteLogin = mock()
        mockOnlineChecker = mock()

        viewModel =
            WelcomeScreenViewModel(
                mockNavigator,
                mockOnlineChecker,
                mockRemoteLogin
            )
    }
}
