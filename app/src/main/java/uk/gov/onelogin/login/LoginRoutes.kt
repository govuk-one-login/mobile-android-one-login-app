package uk.gov.onelogin.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object LoginRoutes {
    const val ROOT: String = "/login"

    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.loginFlowRoutes(state: String) {
        lateinit var welcomeScreenUrl: WelcomeScreenUrl

        navigation(
            route = ROOT,
            startDestination = START,
        ) {
            composable(
                route = START,
            ) {
                WelcomeScreen(
                    state = state
                )
            }
        }
    }
}
