package uk.gov.onelogin.login

import uk.gov.onelogin.navigation.NavRoute

sealed class LoginRoutes(private val route: String) : NavRoute {
    data object Root : LoginRoutes("/login")
    data object Start : LoginRoutes("/login/start")
    data object Welcome : LoginRoutes("/login/welcome")
    data object Loading : LoginRoutes("/login/loading")
    data object PasscodeInfo : LoginRoutes("/login/passcode_info")
    data object BioOptIn : LoginRoutes("/login/bio_opt_in")
    data object SignInError : LoginRoutes("/login/sign_in_error")

    override fun getRoute() = route
}
