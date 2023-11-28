package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.authentication.LoginSession
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.login.LoadingScreen
import uk.gov.onelogin.login.LoginRoutes
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        println("CSG - onCreate")
        super.onCreate(savedInstanceState)

        installSplashScreen()

        val context = this
        val lifecycleOwner = this

        setContent {
            val navController = rememberNavController()

            viewModel.apply {
                next.observe(lifecycleOwner) {
                    navController.navigate(it)
                }
                handleIntent(
                    context = context,
                    intent = intent
                )
            }

            GdsTheme {
                viewModel.appRoutes.routes(
                    navController = navController,
                    startDestination = LoginRoutes.ROOT
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("CSG - onActivityResult")
        if (requestCode == LoginSession.REQUEST_CODE_AUTH) {
            setContent {
                LoadingScreen()
            }
            viewModel.handleIntent(
                context = this,
                intent = data
            )
        }
    }
}
