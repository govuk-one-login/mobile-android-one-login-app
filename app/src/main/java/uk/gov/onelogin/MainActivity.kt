package uk.gov.onelogin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import uk.gov.onelogin.core.navigation.domain.Navigator

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            DisposableEffect(Unit) {
                navigator.setController(navController)

                onDispose {
                    navigator.reset()
                }
            }

            OneLoginApp(navController = navController)
        }
    }

    override fun onStop() {
        super.onStop()
        println("Wallet MainActivity.onStop() called")
    }
}
