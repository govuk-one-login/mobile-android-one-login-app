package uk.gov.onelogin.features.error.ui.auth

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.features.BaseScreenshotTest

@RunWith(Parameterized::class)
class AuthErrorScreenshotTest(
    authErrorViewModel: AuthErrorViewModel,
    nightMode: NightMode
) : BaseScreenshotTest(nightMode) {
    override val generateComposeLayout: @Composable () -> Unit = {
        GdsTheme {
            AuthErrorScreen(authErrorViewModel)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(authErrorViewModelWalletNotEnabled, NOTNIGHT),
                arrayOf(authErrorViewModelWalletNotEnabled, NIGHT),
                arrayOf(authErrorViewModelWalletEnabled, NOTNIGHT),
                arrayOf(authErrorViewModelWalletEnabled, NIGHT)
            )
        }
    }
}
