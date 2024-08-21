package uk.gov.onelogin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.developer.DeveloperRoutes.developerFlowRoutes
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import uk.gov.onelogin.mainnav.nav.MainNavRoutes.mainNavRoutesFlow
import uk.gov.onelogin.signOut.SignOutRoutes.signOutRoute
import uk.gov.onelogin.ui.error.ErrorRoutes.genericErrorRoute

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lifecycleOwner = this
        lifecycle.addObserver(viewModel)

        setContent {
            val navController = rememberNavController()

            GdsTheme {
                NavHost(
                    navController = navController,
                    startDestination = LoginRoutes.ROOT
                ) {
                    loginFlowRoutes(navController)
                    mainNavRoutesFlow(navController)
                    genericErrorRoute(navController)
                    signOutRoute(navController)
                    developerFlowRoutes(navController)
                }
            }

            LaunchedEffect(key1 = Unit) {
                viewModel.next.observe(lifecycleOwner) {
                    navController.navigate(it) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}
