package uk.gov.onelogin.navigation

import androidx.navigation.NavHostController
import uk.gov.onelogin.ui.error.ErrorRoutes

fun NavHostController.hasPreviousBackStack() = this.previousBackStackEntry != null

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

fun NavHostController.navigateToGenericErrorScreen() = this.navigateSingleTopTo(ErrorRoutes.ROOT)

fun NavHostController.navigateToOfflineErrorScreen() = this.navigateSingleTopTo(ErrorRoutes.OFFLINE)
