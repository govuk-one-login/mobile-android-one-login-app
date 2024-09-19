package uk.gov.onelogin.navigation

import androidx.navigation.NavHostController

interface Navigator {
    fun setController(navController: NavHostController)

    fun navigate(route: NavRoute, popUpToInclusive: Boolean = false)

    fun goBack()

    fun hasBackStack(): Boolean

    fun openDeveloperPanel()

    fun reset()
}
