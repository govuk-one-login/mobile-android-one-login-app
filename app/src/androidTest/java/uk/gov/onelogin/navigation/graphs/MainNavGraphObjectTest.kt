package uk.gov.onelogin.navigation.graphs

import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.navigation.graphs.MainNavGraph.mainNavRoutesFlow
import uk.gov.onelogin.utils.TestCase
import uk.gov.onelogin.utils.TestUtils
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppInfoApiModule::class)
class MainNavGraphObjectTest : TestCase() {
    @Inject
    lateinit var navigator: Navigator

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
                startDestination = MainNavRoutes.Root.getRoute(),
            ) {
                mainNavRoutesFlow()
            }
        }

        wheneverBlocking { appInfoService.get() }.thenReturn(
            AppInfoServiceState.Successful(TestUtils.appInfoData),
        )
    }

    @Test
    fun mainGraph_startingDestination() =
        runTest {
            composeTestRule.runOnUiThread {
                navController.setCurrentDestination(MainNavRoutes.Start.getRoute())
            }

            composeTestRule
                .onNodeWithTag(
                    resources.getString(R.string.welcomeCardTestTag),
                ).assertExists()
        }
}
