package uk.gov.onelogin.core.navigation.data

import uk.gov.onelogin.core.navigation.domain.NavRoute

sealed class SettingsRoutes(
    private val route: String
) : NavRoute {
    data object Ossl : SettingsRoutes("/settings/ossl")

    override fun getRoute() = route
}
