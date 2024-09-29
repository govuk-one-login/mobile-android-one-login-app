package uk.gov.onelogin.signOut

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.signOut.ui.SignOutScreen
import uk.gov.onelogin.signOut.ui.SignedOutInfoScreen
import uk.gov.onelogin.ui.error.ErrorGraphObject.OFFLINE_ERROR_TRY_AGAIN_KEY

object SignOutGraphObject {
    fun NavGraphBuilder.signOutGraph(navController: NavHostController) {
        navigation(
            route = SignOutRoutes.Root.getRoute(),
            startDestination = SignOutRoutes.Start.getRoute()
        ) {
            composable(
                route = SignOutRoutes.Start.getRoute()
            ) {
                SignOutScreen()
            }
            composable(
                route = SignOutRoutes.Info.getRoute()
            ) {
                SignedOutInfoScreen(
                    shouldTryAgain = {
                        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                        val tryAgain = savedStateHandle?.get(OFFLINE_ERROR_TRY_AGAIN_KEY) ?: false
                        savedStateHandle?.remove<Boolean>(OFFLINE_ERROR_TRY_AGAIN_KEY)
                        tryAgain
                    }
                )
            }
        }
    }
}
