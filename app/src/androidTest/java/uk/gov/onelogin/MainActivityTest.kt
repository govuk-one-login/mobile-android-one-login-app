package uk.gov.onelogin

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.utils.ActivityProviderModule
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(NavigatorModule::class, ActivityProviderModule::class)
class MainActivityTest : TestCase() {
    @BindValue
    val navigator: Navigator = mock()

    @BindValue
    val activityProvider: ActivityProvider = mock()

    @Inject
    lateinit var featureFlags: FeatureFlags

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testMainActivityLaunch() {
        launchActivity<MainActivity>()
        verify(navigator).setController(any())
        verify(activityProvider).setCurrentActivity(any())
    }

    @Test
    fun testActivityIsSetOnResume() {
        launchActivity<MainActivity>().moveToState(Lifecycle.State.RESUMED)
        verify(activityProvider).setCurrentActivity(any())
    }

    @Test
    fun testActivityIsClearedOnPause() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.DESTROYED)
        }
        verify(activityProvider).clearActivity()
    }
}
