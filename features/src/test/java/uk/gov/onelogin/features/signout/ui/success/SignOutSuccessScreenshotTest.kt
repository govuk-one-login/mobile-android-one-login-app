package uk.gov.onelogin.features.signout.ui.success

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest

@RunWith(Parameterized::class)
class SignOutSuccessScreenshotTest(
    walletEnabled: Boolean,
    nightMode: NightMode
) : BaseScreenshotTest(nightMode) {
    override val generateComposeLayout: @Composable () -> Unit = {
        SignOutConfirmationBody(walletEnabled) {}
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(true, NOTNIGHT),
                arrayOf(true, NIGHT),
                arrayOf(false, NOTNIGHT),
                arrayOf(false, NIGHT)
            )
        }
    }
}
