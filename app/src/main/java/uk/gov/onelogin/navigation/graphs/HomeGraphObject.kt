package uk.gov.onelogin.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import uk.gov.onelogin.core.navigation.data.HomeRoutes
import uk.gov.onelogin.core.ui.dialog.fullScreenDialogProperties
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModal

object HomeGraphObject {
    fun NavGraphBuilder.homeGraph() {
        dialog(
            route = HomeRoutes.HowToProveYourIdentity.getRoute(),
            dialogProperties = fullScreenDialogProperties,
        ) {
            HowToProveYourIdentityModal()
        }
    }
}
