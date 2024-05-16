package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.android.authentication.AppAuthSession
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.login.LoginRoutes

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lifecycleOwner = this
        lifecycle.addObserver(viewModel)

        setContent {
            val navController = rememberNavController()
//          I've changed this to GdsTheme to Material3 - if the Material one will be used, the primary
//          colors for Wallet will be different (purple)
            GdsTheme {
                viewModel.appRoutes.routes(
                    navController = navController,
                    startDestination = LoginRoutes.ROOT
                )
            }

            LaunchedEffect(key1 = Unit) {
                viewModel.apply {
                    next.observe(lifecycleOwner) {
                        navController.navigate(it) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                    handleActivityResult(intent = intent)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppAuthSession.REQUEST_CODE_AUTH) {
            if (data != null) {
                viewModel.handleActivityResult(
                    intent = data
                )
            }
        }
    }
}
