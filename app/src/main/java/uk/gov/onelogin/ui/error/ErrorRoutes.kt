package uk.gov.onelogin.ui.error

import uk.gov.onelogin.navigation.NavRoute

sealed class ErrorRoutes(private val route: String) : NavRoute {
    data object Root : ErrorRoutes("/error")
    data object Generic : ErrorRoutes("/error/generic")
    data object Offline : ErrorRoutes("/error/offline")
    data object SignOut : ErrorRoutes("/error/signOut")

    override fun getRoute() = route
}
