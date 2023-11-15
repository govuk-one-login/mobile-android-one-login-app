package uk.gov.onelogin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.ui.theme.m3.GdsTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isLoading: Boolean = true
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition { isLoading }
        }

        viewModel.isLoading.observe(this) {
            isLoading = it
        }

        viewModel.handleDeepLink(
            data = intent.data
        )

        setContent {
            GdsTheme {
                AppRoutes(
                    navController = rememberNavController()
                )
            }
        }
    }
}
