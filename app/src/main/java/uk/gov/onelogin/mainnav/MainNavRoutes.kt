package uk.gov.onelogin.mainnav

import uk.gov.onelogin.navigation.NavRoute

sealed class MainNavRoutes(private val route: String) : NavRoute {
    data object Root : MainNavRoutes("/home")
    data object Start : MainNavRoutes("/home/start")

    override fun getRoute() = route
}
