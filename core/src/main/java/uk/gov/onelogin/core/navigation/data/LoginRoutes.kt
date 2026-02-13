package uk.gov.onelogin.core.navigation.data

import uk.gov.onelogin.core.navigation.domain.NavRoute

sealed class LoginRoutes(
    private val route: String,
) : NavRoute {
    data object Root : LoginRoutes("/login")

    data object Start : LoginRoutes("/login/start")

    data object Welcome : LoginRoutes("/login/welcome")

    data object Loading : LoginRoutes("/login/loading")

    data object SignInRecoverableError : LoginRoutes("/login/sign_in_recoverable_error")

    data object AnalyticsOptIn : LoginRoutes("/login/analytics_opt_in")

    data object SignInUnrecoverableError : LoginRoutes("/login/sign_in_unrecoverable_error")

    override fun getRoute() = route
}
