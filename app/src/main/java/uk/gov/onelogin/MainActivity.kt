package uk.gov.onelogin

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.theme.GdsTheme

class MainActivity : AppCompatActivity() {
    private lateinit var authCodeExchange: IAuthCodeExchange
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authCodeExchange = AuthCodeExchange(
            context = this,
            httpClient = HttpClient(),
            onlineChecker = OnlineChecker(
                connectivityManager = getSystemService(ConnectivityManager::class.java)
            )
        )

        viewModel = MainActivityViewModel.Companion.Factory(
            authCodeExchange = authCodeExchange,
            context = this
        ).create(MainActivityViewModel::class.java)


        installSplashScreen()

        val lifecycleOwner = this

        setContent {
            val navController = rememberNavController()

            viewModel.apply {
                next.observe(lifecycleOwner) {
                    navController.navigate(it)
                }
                handleDeepLink(
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
