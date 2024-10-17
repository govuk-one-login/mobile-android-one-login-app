package uk.gov.onelogin.navigation

import android.app.Activity
import androidx.navigation.NavHostController

fun NavHostController.hasPreviousBackStack() = this.previousBackStackEntry != null

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

fun NavHostController.closeApp() {
    while (hasPreviousBackStack()) {
        popBackStack()
    }
    (context as? Activity)?.finish()
}
