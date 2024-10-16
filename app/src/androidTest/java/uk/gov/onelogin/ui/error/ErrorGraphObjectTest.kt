package uk.gov.onelogin.ui.error

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.TestUtils.back
import uk.gov.onelogin.TestUtils.setActivity
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class ErrorGraphObjectTest : TestCase() {
    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var navigator: Navigator

    @BindValue
    val mockAppInfoService: AppInfoService = mock()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @Before
    fun setup() {
        hiltRule.inject()

        wheneverBlocking { mockAppInfoService.get() }.thenAnswer {
            AppInfoServiceState.Successful(TestUtils.appInfoData)
        }
    }

    @Test
    fun errorGraph_signOutError() {
        composeTestRule.setActivity {
            navigator.navigate(ErrorRoutes.SignOut)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_signOutErrorTitle)
        ).assertExists()
    }

    @Test
    fun errorGraph_genericError() {
        composeTestRule.setActivity {
            navigator.navigate(ErrorRoutes.Generic)
        }
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_somethingWentWrongErrorTitle)
        ).assertExists()
    }

    @Test
    fun errorGraph_offlineError() {
        composeTestRule.setActivity {
            navigator.navigate(ErrorRoutes.Offline)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_networkErrorTitle)
        ).assertExists()
    }

    @Test
    fun errorGraph_updateRequiredError() {
        composeTestRule.setActivity {
            navigator.navigate(ErrorRoutes.UpdateRequired)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_updateApp_Title)
        ).assertExists()
        composeTestRule.back()
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_updateApp_Title)
        ).assertDoesNotExist()
    }
}
