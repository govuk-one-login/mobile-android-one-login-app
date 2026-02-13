package uk.gov.onelogin.navigation.graphs

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.features.error.ui.auth.AuthErrorScreen
import uk.gov.onelogin.features.signout.ui.SignOutScreen
import uk.gov.onelogin.features.signout.ui.info.SignedOutInfoScreen
import uk.gov.onelogin.features.signout.ui.success.SignOutSuccess
import uk.gov.onelogin.navigation.graphs.ErrorGraphObject.OFFLINE_ERROR_TRY_AGAIN_KEY

object SignOutGraphObject {
    fun NavGraphBuilder.signOutGraph(navController: NavHostController) {
        navigation(
            route = SignOutRoutes.Root.getRoute(),
            startDestination = SignOutRoutes.Start.getRoute(),
        ) {
            composable(
                route = SignOutRoutes.Start.getRoute(),
            ) {
                SignOutScreen()
            }
            composable(
                route = SignOutRoutes.Info.getRoute(),
            ) {
                // I have added this temporarily as it seems if you remove your passcode/ biometrics
                // and press back it would take you back to the HomeScreen
                BackHandler {
                    // Nothing to do
                }
                SignedOutInfoScreen(
                    shouldTryAgain = {
                        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                        val tryAgain = savedStateHandle?.get(OFFLINE_ERROR_TRY_AGAIN_KEY) ?: false
                        savedStateHandle?.remove<Boolean>(OFFLINE_ERROR_TRY_AGAIN_KEY)
                        tryAgain
                    },
                )
            }
            composable(
                route = SignOutRoutes.ReAuthError.getRoute(),
            ) {
                BackHandler(enabled = true) {
                    // always disabled
                }
                AuthErrorScreen()
            }
            composable(
                route = SignOutRoutes.Success.getRoute(),
            ) {
                SignOutSuccess()
            }
        }
    }
}
