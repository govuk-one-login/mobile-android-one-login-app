package uk.gov.onelogin.core.navigation.data

import uk.gov.onelogin.core.navigation.domain.NavRoute

sealed class SignOutRoutes(private val route: String) : NavRoute {
    data object Root : SignOutRoutes("/signOut")

    data object Start : SignOutRoutes("/signOut/start")

    data object Info : SignOutRoutes("/signOut/info")

    data object ReAuthError : SignOutRoutes("/signOut/reAuthError")

    override fun getRoute() = route
}
