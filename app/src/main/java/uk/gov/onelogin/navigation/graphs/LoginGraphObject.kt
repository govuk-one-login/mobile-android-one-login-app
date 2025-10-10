package uk.gov.onelogin.navigation.graphs

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.hasPreviousBackStack
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.features.error.ui.signin.SignInErrorRecoverableScreen
import uk.gov.onelogin.features.error.ui.signin.SignInErrorUnrecoverableScreen
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreen
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreen
import uk.gov.onelogin.features.optin.ui.OptInScreen

object LoginGraphObject {
    @Suppress("LongMethod")
    fun NavGraphBuilder.loginGraph(
        navController: NavHostController
    ) {
        navigation(
            route = LoginRoutes.Root.getRoute(),
            startDestination = LoginRoutes.Start.getRoute()
        ) {
            composable(
                route = LoginRoutes.Start.getRoute()
            ) {
                val comingFromLockScreen = navController.hasPreviousBackStack()
                BackHandler(enabled = comingFromLockScreen) {
                    // do nothing if coming from Lock Screen
                }
                SplashScreen()
            }

            composable(
                route = LoginRoutes.Welcome.getRoute()
            ) {
                WelcomeScreen()
            }

            composable(
                route = LoginRoutes.Loading.getRoute()
            ) {
                LoadingScreen {}
            }

            composable(
                route = LoginRoutes.SignInRecoverableError.getRoute()
            ) {
                SignInErrorRecoverableScreen()
            }

            composable(
                route = LoginRoutes.SignInUnrecoverableError.getRoute()
            ) {
                SignInErrorUnrecoverableScreen()
            }

            composable(
                route = LoginRoutes.AnalyticsOptIn.getRoute()
            ) {
                OptInScreen()
            }
        }
    }
}
