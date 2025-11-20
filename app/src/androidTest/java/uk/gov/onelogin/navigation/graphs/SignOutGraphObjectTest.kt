package uk.gov.onelogin.navigation.graphs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.e2e.LoginTest.Companion.TIMEOUT
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.navigation.graphs.SignOutGraphObject.signOutGraph
import uk.gov.onelogin.utils.TestCase
import uk.gov.onelogin.utils.TestUtils
import uk.gov.onelogin.utils.TestUtils.back

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class SignOutGraphObjectTest : TestCase() {
    @BindValue
    val mockAppInfoService: AppInfoService = mock()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(
                navController = navController,
                startDestination = SignOutRoutes.Root.getRoute()
            ) {
                signOutGraph(navController)
            }
        }

        wheneverBlocking { mockAppInfoService.get() }.thenAnswer {
            AppInfoServiceState.Successful(TestUtils.appInfoData)
        }
    }

    @Test
    fun signOutGraph_startingDestination() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SignOutRoutes.Start.getRoute())
        }

        val signOutTitle = composeTestRule.onNodeWithText(
            resources.getString(R.string.app_signOutConfirmationTitle)
        )
        composeTestRule.waitUntil(TIMEOUT) {
            signOutTitle.isDisplayed()
        }
        signOutTitle.assertIsDisplayed()
    }

    @Test
    fun signOutGraph_navigateToSignedOutInfoScreen() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SignOutRoutes.Info.getRoute())
        }

        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_youveBeenSignedOutTitle)
            ).assertIsDisplayed()

            back()

            onNodeWithText(
                resources.getString(R.string.app_youveBeenSignedOutTitle)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun signOutGraph_navigateToReAuthErrorScreen() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SignOutRoutes.ReAuthError.getRoute())
        }

        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_dataDeletedErrorTitle)
            ).assertIsDisplayed()

            back()

            cancelAndRecreateRecomposer()

            onNodeWithText(
                resources.getString(R.string.app_dataDeletedErrorTitle)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun signOutGraph_navigateToSignOutSuccessScreen() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(SignOutRoutes.Success.getRoute())
        }

        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_signOutTitle)
            ).assertIsDisplayed()

            activityRule.scenario.onActivity { activity ->
                back()

                assertTrue(activity.isFinishing)
            }
        }
    }
}
