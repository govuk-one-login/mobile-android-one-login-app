package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.authentication.LoginSession
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.login.LoginRoutes
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this
        val lifecycleOwner = this

        installSplashScreen()

        setContent {
            navController = rememberNavController()

            GdsTheme {
                viewModel.appRoutes.routes(
                    navController = navController!!,
                    startDestination = LoginRoutes.ROOT
                )
            }

            LaunchedEffect(key1 = Unit) {
                viewModel.apply {
                    next.observe(lifecycleOwner) {
                        navController?.navigate(it)
                    }
                    handleIntent(
                        context = context,
                        intent = intent
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LoginSession.REQUEST_CODE_AUTH) {
            viewModel.handleIntent(
                context = this,
                intent = data
            )
        }
    }
}
