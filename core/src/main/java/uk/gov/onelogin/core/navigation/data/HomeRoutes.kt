package uk.gov.onelogin.core.navigation.data

import uk.gov.onelogin.core.navigation.domain.NavRoute

sealed class HomeRoutes(
    private val route: String,
) : NavRoute {
    data object HowToProveYourIdentity : HomeRoutes("/home/how_to_prove_your_identity")

    override fun getRoute() = route
}
