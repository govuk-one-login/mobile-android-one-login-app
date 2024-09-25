package uk.gov.onelogin.navigation

import androidx.navigation.NavHostController

fun NavHostController.hasPreviousBackStack() = this.previousBackStackEntry != null

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
