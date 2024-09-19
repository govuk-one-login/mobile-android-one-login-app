package uk.gov.onelogin.navigation

import android.util.Log
import androidx.navigation.NavHostController
import javax.inject.Inject
import uk.gov.onelogin.developer.DeveloperRoutes.navigateToDeveloperPanel

class NavigatorImpl @Inject constructor() : Navigator {
    private var navController: NavHostController? = null
        get() {
            if (field == null) Log.w(this::class.simpleName, "Navigator not initialised")
            return field
        }

    override fun setController(navController: NavHostController) {
        this.navController = navController
    }

    override fun navigate(route: NavRoute, popUpToInclusive: Boolean) {
        navController?.let { controller ->
            controller.navigate(route.getRoute()) {
                popUpTo(controller.graph.id) {
                    inclusive = popUpToInclusive
                }
            }
        }
    }

    override fun goBack() {
        navController?.popBackStack()
    }

    override fun hasBackStack(): Boolean = navController?.hasPreviousBackStack() ?: false

    override fun openDeveloperPanel() {
        navController?.navigateToDeveloperPanel()
    }

    override fun reset() {
        navController = null
    }
}
