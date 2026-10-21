package uk.gov.onelogin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.DisposableEffect
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.utils.ActivityProvider

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            val navController = rememberNavController()

            DisposableEffect(Unit) {
                navigator.setController(navController)
                viewModel.initialiseNetworkService()

                onDispose {
                    navigator.reset()
                }
            }

            OneLoginApp(navController = navController)
        }
    }

    override fun onResume() {
        super.onResume()
        activityProvider.setCurrentActivity(this as FragmentActivity)
    }

    override fun onPause() {
        super.onPause()
        activityProvider.clearActivity()
    }
}
