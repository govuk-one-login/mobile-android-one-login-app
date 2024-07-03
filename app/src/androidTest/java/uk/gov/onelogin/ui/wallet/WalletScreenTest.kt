package uk.gov.onelogin.ui.wallet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class WalletScreenTest : TestCase() {
    @Before
    fun setUp() {
        composeTestRule.setContent {
            WalletScreen()
        }
    }

    @Test
    fun genericErrorScreen() {
        composeTestRule.onNode(
            hasText(
                resources.getString(R.string.app_walletTitle)
            )
        ).assertIsDisplayed()
    }
}
