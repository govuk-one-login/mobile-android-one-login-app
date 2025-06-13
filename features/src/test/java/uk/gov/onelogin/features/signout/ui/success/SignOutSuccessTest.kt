package uk.gov.onelogin.features.signout.ui.success

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@RunWith(AndroidJUnit4::class)
class SignOutSuccessTest : FragmentActivityTestCase() {
    private lateinit var navigator: Navigator
    private lateinit var featureFlags: FeatureFlags
    private lateinit var viewModel: SignOutSuccessViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignOutSuccessAnalyticsViewModel

    private val title = hasText(context.getString(R.string.app_signOutTitle))
    private val button = hasText(context.getString(R.string.app_signOutSuccessButton))
    private val bodyNoWallet = hasText(context.getString(R.string.app_signOutBody))
    private val bodyNoWallet2 = hasText(context.getString(R.string.app_signOutBody2))
    private val bodyWallet = hasText(context.getString(R.string.app_signOutWalletBody))
    private val bodyWallet2 = hasText(context.getString(R.string.app_signOutWalletBody2))

    @Before
    fun setup() {
        navigator = mock()
        featureFlags = mock()
        viewModel = SignOutSuccessViewModel(navigator, featureFlags)
        analytics = mock()
        analyticsViewModel = SignOutSuccessAnalyticsViewModel(context, analytics)
    }

    @Test
    fun testDisplayNoWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        setupContent(viewModel, analyticsViewModel)

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyNoWallet).assertIsDisplayed()
            onNode(bodyNoWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }

        verify(analytics)
            .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makeNoWalletScreenEvent(context))
    }

    @Test
    fun testDisplayWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)
        setupContent(viewModel, analyticsViewModel)

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyWallet).assertIsDisplayed()
            onNode(bodyWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }

        verify(analytics)
            .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makeWalletScreenEvent(context))
    }

    @Test
    fun onPrimary() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        setupContent(viewModel, analyticsViewModel)
        composeTestRule.apply {
            onNode(button).assertIsDisplayed().performClick()
        }

        verify(analytics)
            .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makePrimaryEvent(context))
        verify(analytics)
            .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makeNoWalletScreenEvent(context))
        verify(navigator).navigate(LoginRoutes.Root, true)
    }

    @Test
    fun onBackButton() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        setupContent(viewModel, analyticsViewModel)

        composeTestRule.activityRule.scenario.onActivity { activity ->
            Espresso.pressBack()

            verify(analytics)
                .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makeNoWalletScreenEvent(context))
            verify(analytics)
                .logEventV3Dot1(SignOutSuccessAnalyticsViewModel.makeBackPressed(context))
            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun testPreviewNoWallet() {
        composeTestRule.setContent {
            SignOutSuccessPreview()
        }

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyNoWallet).assertIsDisplayed()
            onNode(bodyNoWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }
    }

    @Test
    fun testPreviewWallet() {
        composeTestRule.setContent {
            SignOutSuccessWalletPreview()
        }

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyWallet).assertIsDisplayed()
            onNode(bodyWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }
    }

    private fun setupContent(
        viewModel: SignOutSuccessViewModel,
        analyticsViewModel: SignOutSuccessAnalyticsViewModel
    ) {
        composeTestRule.setContent {
            SignOutSuccess(viewModel, analyticsViewModel)
        }
    }
}
