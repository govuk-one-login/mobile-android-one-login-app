package uk.gov.onelogin.features.error.ui.auth

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest

@RunWith(Parameterized::class)
class AuthErrorScreenshotWalletTest(nightMode: NightMode, locale: String) :
    BaseScreenshotTest(nightMode, locale) {
    override val generateComposeLayout: @Composable () -> Unit = {
        AuthErrorScreenPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> {
            return applyNightModeAndLocale()
        }
    }
}
