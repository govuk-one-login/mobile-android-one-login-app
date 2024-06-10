package uk.gov.onelogin.mainnav.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.mainnav.nav.BottomNavDestination

@HiltAndroidTest
class MainNavScreenTest : TestCase() {
    private val homeTab = hasText(resources.getString(R.string.app_home))
    private val walletTab = hasText(resources.getString(R.string.app_wallet))
    private val profileTab = hasText(resources.getString(R.string.app_profile))

    @Before
    fun setup() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MainNavScreen(navController)
        }
    }

    @Test
    fun checkBottomOptionsDisplayed() {
        composeTestRule.onAllNodes(homeTab)[1].isDisplayed() // we have double match of `Home` text
        composeTestRule.onNode(walletTab).isDisplayed()
        composeTestRule.onNode(profileTab).isDisplayed()

        assertEquals(
            BottomNavDestination.Home.key,
            navController.currentDestination?.route
        )
    }

    @Test
    fun goesToWalletOnClick() {
        composeTestRule.onNode(walletTab).performClick()

        assertEquals(
            BottomNavDestination.Wallet.key,
            navController.currentDestination?.route
        )
    }

    @Test
    fun goesToProfileOnClick() {
        composeTestRule.onNode(profileTab).performClick()

        assertEquals(
            BottomNavDestination.Profile.key,
            navController.currentDestination?.route
        )
    }
}
