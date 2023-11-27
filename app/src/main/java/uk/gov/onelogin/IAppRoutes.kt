package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface IAppRoutes {
    @Composable
    fun routes(
        navController: NavHostController,
        startDestination: String
    )
}
