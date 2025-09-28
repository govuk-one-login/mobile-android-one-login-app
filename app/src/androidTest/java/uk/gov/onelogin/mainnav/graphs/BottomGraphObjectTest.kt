package uk.gov.onelogin.mainnav.graphs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.core.R
import uk.gov.android.wallet.core.R as walletR
import uk.gov.onelogin.mainnav.graphs.BottomNavGraph.bottomGraph
import uk.gov.onelogin.mainnav.ui.BottomNavDestination
import uk.gov.onelogin.utils.TestCase

@HiltAndroidTest
class BottomGraphObjectTest : TestCase() {
    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(
                navController = navController,
                startDestination = BottomNavDestination.Home.key
            ) {
                bottomGraph { false }
            }
        }
    }

    @Test
    fun navigateToHome() {
        composeTestRule.runOnUiThread { navController.navigate(BottomNavDestination.Home.key) }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_welcomeTileHeader)
        ).assertExists()
    }

    @Test
    fun navigateToWallet() {
        composeTestRule.runOnUiThread {
            navController.navigate(BottomNavDestination.Wallet.key + "/false")
        }

        composeTestRule.onNodeWithText(
            resources.getString(walletR.string.intro_card_title)
        ).assertIsDisplayed()
    }

    @Test
    fun navigateToSettings() {
        composeTestRule.runOnUiThread { navController.navigate(BottomNavDestination.Settings.key) }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_settings)
        ).assertExists()
    }
}
