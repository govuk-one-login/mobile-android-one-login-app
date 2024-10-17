package uk.gov.onelogin.signOut

import androidx.compose.ui.test.assertIsDisplayed
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
class SignOutGraphObjectTest : TestCase() {
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
    fun signOutGraph_startingDestination() {
        composeTestRule.setActivity {
            navigator.navigate(SignOutRoutes.Start)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_signOutConfirmationTitle)
        ).assertIsDisplayed()
    }

    @Test
    fun signOutGraph_navigateToSignedOutInfoScreen() {
        composeTestRule.setActivity {
            navigator.navigate(SignOutRoutes.Info)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_youveBeenSignedOutTitle)
        ).assertIsDisplayed()
    }

    @Test
    fun signOutGraph_navigateToReAuthErrorScreen() {
        composeTestRule.setActivity {
            navigator.navigate(SignOutRoutes.ReAuthError)
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
}
