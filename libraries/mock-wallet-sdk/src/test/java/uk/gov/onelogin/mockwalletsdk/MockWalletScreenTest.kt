package uk.gov.onelogin.mockwalletsdk

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import uk.gov.android.wallet.sdk.WalletSdk

@RunWith(AndroidJUnit4::class)
class MockWalletScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val config: WalletSdk.ConfigurationV2 = mock()
    private val displayAsFullScreen = MutableStateFlow(false)
    private val mockWalletSdk = MockWalletSdk(config, displayAsFullScreen)

    @Test
    fun `displays initial mock wallet content via WalletApp`() {
        composeTestRule.setContent {
            mockWalletSdk.WalletApp()
        }

        composeTestRule
            .onNodeWithText("Mock Wallet (Test Build)")
            .assertIsDisplayed()
    }

    @Test
    fun `displays MockWalletContent directly`() {
        composeTestRule.setContent {
            MockWalletContent()
        }

        composeTestRule
            .onNodeWithText("Mock Wallet (Test Build)")
            .assertIsDisplayed()
    }
}
