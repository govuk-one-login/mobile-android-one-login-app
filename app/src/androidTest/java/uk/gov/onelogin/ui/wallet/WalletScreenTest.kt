package uk.gov.onelogin.ui.wallet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Test
import uk.gov.android.wallet.core.R
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class WalletScreenTest : TestCase() {
    @Inject
    lateinit var walletSdk: WalletSdk

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.setContent {
            walletSdk.WalletApp(deeplink = "", adminEnabled = false)
        }
    }

    @Test
    fun genericErrorScreen() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.introCardTitle)
            ).assertIsDisplayed()
        }
    }
}
