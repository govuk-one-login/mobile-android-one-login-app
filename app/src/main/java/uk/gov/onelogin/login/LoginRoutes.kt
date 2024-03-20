package uk.gov.onelogin.login

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.login.ui.BiometricsOptInScreen
import uk.gov.onelogin.login.ui.LoadingScreen
import uk.gov.onelogin.login.ui.PasscodeInfoScreen
import uk.gov.onelogin.login.ui.WelcomeScreen
import uk.gov.onelogin.ui.error.ErrorRoutes.OFFLINE_ERROR_TRY_AGAIN_KEY
import uk.gov.onelogin.ui.error.ErrorRoutes.navigateToOfflineErrorScreen
import uk.gov.onelogin.ui.home.HomeRoutes

object LoginRoutes {
    fun NavGraphBuilder.loginFlowRoutes(
        navController: NavHostController
    ) {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                WelcomeScreen(
                    navigateToOfflineErrorScreen = {
                        navController.navigateToOfflineErrorScreen()
                    },
                    shouldTryAgain = {
                        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                        val tryAgain = savedStateHandle?.get(OFFLINE_ERROR_TRY_AGAIN_KEY) ?: false
                        savedStateHandle?.remove<Boolean>(OFFLINE_ERROR_TRY_AGAIN_KEY)
                        tryAgain
                    }
                )
            }

            composable(
                route = LOADING
            ) {
                LoadingScreen()
            }

            composable(
                route = PASSCODE_INFO
            ) {
                PasscodeInfoScreen {
                    navController.navigate(HomeRoutes.START)
                }
            }

            composable(
                route = BIO_OPT_IN
            ) {
                BackHandler(true) {
                    // do nothing
                }
                BiometricsOptInScreen(onPrimary = {
                    navController.navigate(HomeRoutes.START)
                }, onSecondary = {
                    navController.navigate(HomeRoutes.START)
                })
            }
        }
    }

    const val ROOT: String = "/login"
    const val START: String = "$ROOT/start"
    const val LOADING: String = "$ROOT/loading"
    const val PASSCODE_INFO: String = "$ROOT/passcode_error"
    const val BIO_OPT_IN: String = "$ROOT/bioOptIn"
}
