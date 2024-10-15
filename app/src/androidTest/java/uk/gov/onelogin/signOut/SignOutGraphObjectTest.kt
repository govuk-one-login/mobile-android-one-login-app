package uk.gov.onelogin.signOut

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
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class SignOutGraphObjectTest : TestCase() {
    @Inject
    lateinit var navigator: Navigator

    @BindValue
    val mockAppInfoService: AppInfoService = mock()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @Before
    fun setup() {
        hiltRule.inject()
        launchActivity<MainActivity>()

        wheneverBlocking { mockAppInfoService.get() }.thenAnswer {
            AppInfoServiceState.Successful(TestUtils.data)
        }
    }

    @Test
    fun signOutGraph_startingDestination() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.Start)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_signOutConfirmationTitle))
        )
    }

    @Test
    fun signOutGraph_navigateToSignedOutInfoScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.Info)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_youveBeenSignedOutTitle))
        )
    }

    @Test
    fun signOutGraph_navigateToReAuthErrorScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.ReAuthError)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_dataDeletedErrorTitle))
        )

        phoneController.pressBack()
        phoneController.navigateToApp(packageName = "uk.gov.android.onelogin")

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_dataDeletedErrorTitle))
        )
    }
}
