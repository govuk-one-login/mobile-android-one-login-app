package uk.gov.onelogin.features.wallet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.mock
import uk.gov.android.wallet.core.R
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.TestCase

class WalletScreenTest : TestCase() {
    private lateinit var walletSdk: WalletSdk

    @Before
    fun setUp() {
        walletSdk = mock()
        composeTestRule.setContent {
            walletSdk.WalletApp(deeplink = "", adminEnabled = false)
        }
    }

    @Ignore("Provisionally - I'll make this work on Monday")
    @Test
    fun walletScreen() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.introCardTitle),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }
}
