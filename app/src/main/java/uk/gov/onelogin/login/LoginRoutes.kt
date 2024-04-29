package uk.gov.onelogin.login

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.developer.DeveloperRoutes.navigateToDeveloperPanel
import uk.gov.onelogin.login.ui.LoadingScreen
import uk.gov.onelogin.login.ui.PasscodeInfoScreen
import uk.gov.onelogin.login.ui.biooptin.BiometricsOptInScreen
import uk.gov.onelogin.login.ui.splash.SplashScreen
import uk.gov.onelogin.login.ui.welcome.WelcomeScreen
import uk.gov.onelogin.mainnav.nav.MainNavRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes.OFFLINE_ERROR_TRY_AGAIN_KEY
import uk.gov.onelogin.ui.error.ErrorRoutes.navigateToOfflineErrorScreen

object LoginRoutes {
    @Suppress("LongMethod")
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
                val comingFromLockScreen = navController.hasPreviousBackStack()
                BackHandler(enabled = comingFromLockScreen) {
                    // do nothing if coming from Lock Screen
                }
                SplashScreen(
                    fromLockScreen = comingFromLockScreen,
                    nextScreen = splashScreenNavHandler(navController),
                    openDeveloperPanel = { navController.navigateToDeveloperPanel()}
                )
            }

            composable(
                route = WELCOME
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
                    },
                    openDeveloperPanel = {
                        navController.navigateToDeveloperPanel()
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
                    navController.navigate(MainNavRoutes.START)
                }
            }

            composable(
                route = BIO_OPT_IN
            ) {
                BackHandler(true) {
                    // do nothing
                }
                BiometricsOptInScreen(onPrimary = {
                    navController.navigate(MainNavRoutes.START) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }, onSecondary = {
                    navController.navigate(MainNavRoutes.START) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                })
            }
        }
    }

    private fun splashScreenNavHandler(
        navController: NavHostController
    ): (String) -> Unit = {
        val comingFromLockScreen = navController.hasPreviousBackStack()
        val authSuccessful = it == MainNavRoutes.START
        if (comingFromLockScreen && authSuccessful) {
            navController.popBackStack()
        } else {
            navController.navigate(it) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    private fun NavHostController.hasPreviousBackStack() = this.previousBackStackEntry != null

    const val ROOT: String = "/login"
    const val START: String = "$ROOT/start"
    const val WELCOME: String = "$ROOT/welcome"
    const val LOADING: String = "$ROOT/loading"
    const val PASSCODE_INFO: String = "$ROOT/passcode_error"
    const val BIO_OPT_IN: String = "$ROOT/bioOptIn"
}
