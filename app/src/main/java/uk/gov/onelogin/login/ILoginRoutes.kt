package uk.gov.onelogin.login

import androidx.navigation.NavGraphBuilder

interface ILoginRoutes {
    fun loginFlowRoutes(
        navGraphBuilder: NavGraphBuilder,
        state: String
    )
}
