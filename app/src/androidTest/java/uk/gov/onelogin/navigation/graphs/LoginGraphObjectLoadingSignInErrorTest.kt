package uk.gov.onelogin.navigation.graphs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.ui.pages.loading.LOADING_SCREEN_PROGRESS_INDICATOR
import uk.gov.onelogin.e2e.LoginTest.Companion.TIMEOUT
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.navigation.graphs.LoginGraphObject.loginGraph
import uk.gov.onelogin.utils.TestCase
import uk.gov.onelogin.utils.TestUtils.appInfoData

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class LoginGraphObjectLoadingSignInErrorTest : TestCase() {

    @BindValue
    val appInfoService: AppInfoService = mock()

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
                startDestination = LoginRoutes.Root.getRoute()
            ) {
                loginGraph(navController)
            }
        }

        wheneverBlocking { appInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
    }

    @Test
    fun loginGraph_Loading() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(LoginRoutes.Loading.getRoute())
        }

        val progressIndicator = composeTestRule.onNodeWithTag(
            LOADING_SCREEN_PROGRESS_INDICATOR
        )
        composeTestRule.waitUntil(TIMEOUT) {
            progressIndicator.isDisplayed()
        }
        progressIndicator.assertIsDisplayed()
    }

    @Test
    fun loginGraph_SignInError() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(LoginRoutes.SignInRecoverableError.getRoute())
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_signInErrorTitle)
        ).assertIsDisplayed()
    }
}
