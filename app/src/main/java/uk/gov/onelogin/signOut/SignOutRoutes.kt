package uk.gov.onelogin.signOut

import uk.gov.onelogin.navigation.NavRoute

sealed class SignOutRoutes(private val route: String) : NavRoute {
    data object Root : SignOutRoutes("/signOut")
    data object Start : SignOutRoutes("/signOut/start")
    data object Info : SignOutRoutes("/signOut/info")
    override fun getRoute() = route
}
