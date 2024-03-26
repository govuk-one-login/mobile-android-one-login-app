package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.authentication.AppAuthSession
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.login.LoginRoutes

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lifecycleOwner = this

        installSplashScreen()

        setContent {
            val navController = rememberNavController()

            GdsTheme {
                viewModel.appRoutes.routes(
                    navController = navController,
                    startDestination = LoginRoutes.ROOT
                )
            }

            LaunchedEffect(key1 = Unit) {
                viewModel.apply {
                    next.observe(lifecycleOwner) {
                        navController.navigate(it)
                    }
                    handleIntent(
                        intent = intent
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppAuthSession.REQUEST_CODE_AUTH) {
            viewModel.handleIntent(
                intent = data
            )
        }
    }
}
