package uk.gov.onelogin

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.network.auth.AuthCodeExchange
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.network.http.HttpClient
import uk.gov.onelogin.network.utils.OnlineChecker

class MainActivity : AppCompatActivity() {
    private var isLoading: Boolean = true
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
