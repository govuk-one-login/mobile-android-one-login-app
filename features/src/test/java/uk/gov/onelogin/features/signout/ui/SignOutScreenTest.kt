package uk.gov.onelogin.features.signout.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.ui.pages.loading.LOADING_SCREEN_PROGRESS_INDICATOR
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl

@RunWith(AndroidJUnit4::class)
class SignOutScreenTest : FragmentActivityTestCase() {
    private lateinit var analytics: AnalyticsLogger
    private lateinit var loadingAnalyticsVM: LoadingScreenAnalyticsViewModel
    private lateinit var navigator: Navigator
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var viewModel: SignOutViewModel
    private lateinit var analyticsViewModel: SignOutAnalyticsViewModel
    private lateinit var title: SemanticsMatcher
    private lateinit var button: SemanticsMatcher
    private lateinit var closeButton: SemanticsMatcher
    private val logger = SystemLogger()

    @Before
    fun setup() {
        analytics = mock()
        loadingAnalyticsVM = LoadingScreenAnalyticsViewModel(context, analytics)
        navigator = mock()
        signOutUseCase = mock()
        analyticsViewModel = SignOutAnalyticsViewModel(context, analytics)
        title = hasText(resources.getString(R.string.app_signOutConfirmationTitle))
        button = hasText(resources.getString(R.string.app_signOutAndDeleteAppDataButton))
        closeButton = hasContentDescription("Close")
    }

    @Test
    fun verifyScreenDisplayed() {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(
            SignOutAnalyticsViewModel.makeSignOutViewEvent(
                context
            )
        )
    }

    @Test
    fun verifySignOutButtonSucceeds() = runBlocking {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        composeTestRule.onNode(button).performClick()

        composeTestRule.onNodeWithTag(LOADING_SCREEN_PROGRESS_INDICATOR).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(SignOutAnalyticsViewModel.onPrimaryEvent(context))
        verify(signOutUseCase).invoke()
        verify(navigator).navigate(SignOutRoutes.Success)
    }

    @Test
    fun verifySignOutButtonFails() = runTest {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        whenever(signOutUseCase.invoke())
            .thenThrow(SignOutError(Exception("something went wrong")))
        composeTestRule.onNode(button).performClick()
        verify(signOutUseCase).invoke()
        verify(navigator).navigate(ErrorRoutes.SignOutError, false)
    }

    @Test
    fun verifySignOutButtonFailsWithWalletError() = runTest {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        whenever(signOutUseCase.invoke())
            .thenThrow(SignOutError(DeleteWalletDataUseCaseImpl.DeleteWalletDataError()))
        composeTestRule.onNode(button).performClick()
        verify(signOutUseCase).invoke()
        verify(navigator).navigate(ErrorRoutes.SignOutWalletError, false)
    }

    @Test
    fun verifyCloseIconButton() {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        composeTestRule.onNode(closeButton).performClick()

        verify(analytics).logEventV3Dot1(SignOutAnalyticsViewModel.onCloseIcon())
        verify(navigator).goBack()
    }

    @Test
    fun verifyBackButton() {
        viewModel = SignOutViewModel(navigator, signOutUseCase, logger)
        composeTestRule.setContent {
            SignOutScreen(viewModel, analyticsViewModel, loadingAnalyticsVM)
        }
        Espresso.pressBack()

        verify(analytics).logEventV3Dot1(SignOutAnalyticsViewModel.onBackPressed())
        verify(navigator).goBack()
    }
}
