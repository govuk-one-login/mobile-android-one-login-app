package uk.gov.onelogin.mainnav.graphs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
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
import uk.gov.android.ui.componentsv2.R as componentsR

@HiltAndroidTest
class SettingsGraphObjectTest : TestCase() {
    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            navController.setLifecycleOwner(LocalLifecycleOwner.current)
            navController.setViewModelStore(LocalViewModelStoreOwner.current!!.viewModelStore)
            NavHost(
                navController = navController,
                startDestination = SettingsRoutes.Ossl.getRoute(),
            ) {
                settingsGraph(navController)
            }
        }
    }

    @Test
    fun navigateToOssl() {
        composeTestRule
            .onNodeWithText(
                resources.getString(R.string.app_osslTitle),
            ).assertIsDisplayed()

        composeTestRule.runOnUiThread {
            navController.navigate(SettingsRoutes.BiometricsOptIn.getRoute())
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigate(SettingsRoutes.Ossl.getRoute())
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(
                context.getString(componentsR.string.back_icon_button),
            ).performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(
                resources.getString(R.string.app_biometricsToggleTitle),
            ).assertIsDisplayed()
    }

    @Test
    fun navigateToBiometricsOptIn() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SettingsRoutes.BiometricsOptIn.getRoute())
        }

        // Screen title is displayed
        composeTestRule
            .onNodeWithText(
                context.getString(R.string.app_biometricsToggleTitle),
                substring = true,
            ).assertIsDisplayed()
    }
}
