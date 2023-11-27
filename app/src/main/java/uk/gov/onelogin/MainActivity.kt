package uk.gov.onelogin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.ui.theme.GdsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
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
                    data = intent.data
                )
            }

            GdsTheme {
                AppRoutes(
                    navController = navController
                )
            }
        }
    }
}
