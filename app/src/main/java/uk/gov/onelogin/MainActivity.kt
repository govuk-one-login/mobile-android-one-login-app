package uk.gov.onelogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.handleIntent(intent)

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        viewModel.handleIntent(intent)
    }
}
