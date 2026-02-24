package uk.gov.onelogin.core.navigation.data

import uk.gov.onelogin.core.navigation.domain.NavRoute

sealed class SignOutRoutes(
    private val route: String,
) : NavRoute {
    data object Root : SignOutRoutes("/sign_out")

    data object Start : SignOutRoutes("/sign_out/start")

    data object ReAuth : SignOutRoutes("/sign_out/re_auth")

    data object ReAuthError : SignOutRoutes("/sign_out/re_auth_error")

    data object Success : SignOutRoutes("sign_out/success")

    override fun getRoute() = route
}
