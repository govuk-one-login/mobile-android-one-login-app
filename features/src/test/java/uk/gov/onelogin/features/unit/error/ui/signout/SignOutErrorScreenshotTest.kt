package uk.gov.onelogin.features.unit.error.ui.signout

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.error.ui.signout.SignOutPreview

@RunWith(Parameterized::class)
class SignOutErrorScreenshotTest(
    nightMode: NightMode,
    locale: String
) : BaseScreenshotTest(nightMode, locale) {
    override val generateComposeLayout: @Composable () -> Unit = {
        SignOutPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> = applyNightModeAndLocale()
    }
}
