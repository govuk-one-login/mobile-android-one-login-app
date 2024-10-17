package uk.gov.onelogin.mainnav.nav

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
import uk.gov.onelogin.TestUtils.setActivity
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class MainNavGraphObjectTest : TestCase() {
    @Inject
    lateinit var navigator: Navigator

    @BindValue
    val appInfoService: AppInfoService = mock()

    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @Before
    fun setup() {
        hiltRule.inject()

        wheneverBlocking { appInfoService.get() }.thenReturn(
            AppInfoServiceState.Successful(TestUtils.appInfoData)
        )
    }

    @Test
    fun mainGraph_startingDestination() {
        composeTestRule.setActivity {
            navigator.navigate(MainNavRoutes.Start)
        }

        composeTestRule.onAllNodesWithText(
            resources.getString(R.string.app_homeTitle)
        ).assertCountEquals(2)
    }
}
