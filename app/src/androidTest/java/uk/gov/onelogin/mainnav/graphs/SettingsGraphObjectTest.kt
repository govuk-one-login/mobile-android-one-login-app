package uk.gov.onelogin.mainnav.graphs

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.mainnav.graphs.SettingsNavGraph.settingsGraph
import uk.gov.onelogin.utils.TestCase

@HiltAndroidTest
class SettingsGraphObjectTest : TestCase() {
    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(
                navController = navController,
                startDestination = SettingsRoutes.Ossl.getRoute()
            ) {
                settingsGraph(navController)
            }
        }
    }

    @Test
    fun navigateToOssl() {
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_osslTitle)
        ).assertIsDisplayed()
    }

    @Test
    fun navigateToBiometricsOptIn() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SettingsRoutes.BiometricsOptIn.getRoute())
        }

        // Screen title and toggle label
        composeTestRule.onAllNodesWithText(
            context.getString(R.string.app_biometricsToggleTitle),
            substring = true
        ).assertCountEquals(2)
    }
}
