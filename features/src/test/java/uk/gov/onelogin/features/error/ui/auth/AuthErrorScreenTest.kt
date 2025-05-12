package uk.gov.onelogin.features.error.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@RunWith(AndroidJUnit4::class)
class AuthErrorScreenTest : FragmentActivityTestCase() {
    private lateinit var viewModel: AuthErrorViewModel
    private val navigator: Navigator = mock()
    private lateinit var featureFlags: FeatureFlags

    private val title = hasText(resources.getString(R.string.app_dataDeletedErrorTitle))
    private val body1 = hasText(resources.getString(R.string.app_dataDeletedBody1))
    private val body2 = hasText(resources.getString(R.string.app_dataDeletedBody2))
    private val body3 = hasText(resources.getString(R.string.app_dataDeletedBody3))
    private val body1NoWallet =
        hasText(
            resources.getString(R.string.app_dataDeletedBody1_no_wallet)
        )
    private val body2NoWallet =
        hasText(
            resources.getString(R.string.app_dataDeletedBody2_no_wallet)
        )
    private val body3NoWallet =
        hasText(
            resources.getString(R.string.app_dataDeletedBody3_no_wallet)
        )
    private val primary = hasText(resources.getString(R.string.app_dataDeletedButton))

    @Test
    fun reAuthErrorScreenWithWallet() {
        featureFlags =
            InMemoryFeatureFlags(
                setOf(WalletFeatureFlag.ENABLED)
            )
        viewModel = AuthErrorViewModel(navigator, featureFlags)
        composeTestRule.setContent {
            AuthErrorScreen(viewModel)
        }

        composeTestRule.apply {
            onNodeWithContentDescription(
                context.getString(R.string.app_dataDeletedError_ContentDescription)
            ).assertIsDisplayed()

            onNode(title).assertIsDisplayed()
            onNode(body1).assertIsDisplayed()
            onNode(body2).assertIsDisplayed()
            onNode(body3).assertIsDisplayed()
            onNode(primary).assertIsDisplayed()
        }
    }

    @Test
    fun reAuthErrorScreenWithoutWallet() {
        featureFlags =
            InMemoryFeatureFlags(
                setOf()
            )
        viewModel = AuthErrorViewModel(navigator, featureFlags)
        composeTestRule.setContent {
            AuthErrorScreen(viewModel)
        }

        composeTestRule.apply {
            onNodeWithContentDescription(
                context.getString(R.string.app_dataDeletedError_ContentDescription)
            ).assertIsDisplayed()

            onNode(title).assertIsDisplayed()
            onNode(body1NoWallet).assertIsDisplayed()
            onNode(body2NoWallet).assertIsDisplayed()
            onNode(body3NoWallet).assertIsDisplayed()
            onNode(primary).assertIsDisplayed()
        }
    }

    @Test
    fun onPrimary() {
        featureFlags =
            InMemoryFeatureFlags(
                setOf()
            )
        viewModel = AuthErrorViewModel(navigator, featureFlags)
        composeTestRule.setContent {
            AuthErrorScreen(viewModel)
        }

        composeTestRule
            .onNode(primary)
            .performClick()

        verify(navigator).navigate(LoginRoutes.Start, true)
    }

    @Test
    fun noWalletPreview() {
        composeTestRule.setContent {
            AuthErrorScreenNoWalletPreview()
        }
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            AuthErrorScreenPreview()
        }
    }
}
