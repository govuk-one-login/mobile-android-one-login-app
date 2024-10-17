package uk.gov.onelogin.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.FlakyTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.TestUtils.back
import uk.gov.onelogin.TestUtils.data
import uk.gov.onelogin.TestUtils.setActivity
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.login.ui.LOADING_SCREEN_PROGRESS_INDICATOR
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class LoginGraphObjectTest : TestCase() {
    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var navigator: Navigator

    @BindValue
    val appInfoService: AppInfoService = mock()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @Before
    fun setup() {
        hiltRule.inject()

        wheneverBlocking { appInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(data))
    }

    @FlakyTest
    @Test
    fun loginGraph_SignInError() {
        composeTestRule.setActivity { navigator.navigate(LoginRoutes.SignInError) }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_signInErrorTitle)
        ).assertIsDisplayed()
    }

    @FlakyTest
    @Test
    fun loginGraph_BioOptInScreen() {
        composeTestRule.setActivity { navigator.navigate(LoginRoutes.BioOptIn) }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_enableBiometricsTitle)
        ).assertIsDisplayed()
        composeTestRule.back()
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_enableBiometricsTitle)
        ).assertIsDisplayed()
        composeTestRule.back()
    }

    @FlakyTest
    @Test
    fun loginGraph_AnalyticsOptInScreen() {
        composeTestRule.setActivity {
            navigator.navigate(LoginRoutes.AnalyticsOptIn)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_analyticsPermissionBody)
        ).assertIsDisplayed()
        composeTestRule.back()
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_analyticsPermissionBody)
        ).assertIsDisplayed()
    }

    @FlakyTest
    @Test
    fun loginGraph_PasscodeInfo_Button() {
        composeTestRule.setActivity {
            navigator.navigate(LoginRoutes.PasscodeInfo)
        }

        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_noPasscodePatternSetupTitle)
            ).assertIsDisplayed()
            onNodeWithText(resources.getString(R.string.app_continue)).performClick()
            onNodeWithText(
                resources.getString(R.string.app_home)
            )
        }
    }

    @FlakyTest
    @Test
    fun loginGraph_Loading() {
        composeTestRule.setActivity {
            navigator.navigate(LoginRoutes.Loading)
        }

        composeTestRule.onNodeWithTag(
            LOADING_SCREEN_PROGRESS_INDICATOR
        ).assertIsDisplayed()
    }
}
