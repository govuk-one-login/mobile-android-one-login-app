package uk.gov.onelogin.features.login.ui.splash

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.login.ui.signin.splash.UnlockScreenPreview

@RunWith(Parameterized::class)
class SplashScreenshotUnlockTest(nightMode: NightMode) : BaseScreenshotTest(nightMode) {
    override val generateComposeLayout: @Composable () -> Unit = {
        UnlockScreenPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(NOTNIGHT),
                arrayOf(NIGHT)
            )
        }
    }
}
