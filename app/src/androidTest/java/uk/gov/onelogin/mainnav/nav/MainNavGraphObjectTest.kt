package uk.gov.onelogin.mainnav.nav

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
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

    @Before
    fun setup() {
        hiltRule.inject()
        launchActivity<MainActivity>()
    }

    @Test
    fun mainGraph_startingDestination() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(MainNavRoutes.Start)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_homeTitle))
        )
    }
}
