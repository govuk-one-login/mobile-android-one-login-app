package uk.gov.onelogin.core.ui.pages.loading

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.core.test.screenshot.BaseScreenshotTest

@RunWith(Parameterized::class)
class LoadingScreenScreenshotTest(
    nightMode: NightMode,
    locale: String,
    fontScale: Float,
) : BaseScreenshotTest(nightMode, locale, fontScale) {
    override val generateComposeLayout: @Composable () -> Unit = {
        LoadingPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> = applyLightDarkWelshAndFontScale()
    }
}
