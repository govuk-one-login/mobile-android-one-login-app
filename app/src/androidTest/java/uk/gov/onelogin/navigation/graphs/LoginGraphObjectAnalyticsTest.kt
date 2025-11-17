package uk.gov.onelogin.navigation.graphs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.utils.MATestCase
import uk.gov.onelogin.utils.TestUtils.appInfoData
import uk.gov.onelogin.utils.TestUtils.back
import uk.gov.onelogin.utils.TestUtils.setActivity

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class LoginGraphObjectAnalyticsTest : MATestCase() {
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
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
    }

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
            resources.getString(R.string.app_signInButton)
        ).assertIsDisplayed()
    }
}
