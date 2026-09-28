package uk.gov.onelogin.features.unit.home.idcheck.ui

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.core.test.screenshot.BaseScreenshotTest
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityCard

@RunWith(Parameterized::class)
class HowToProveYourIdentityCardScreenshotTest(
    nightMode: NightMode,
    locale: String,
    fontScale: Float,
) : BaseScreenshotTest(nightMode, locale, fontScale) {
    override val generateComposeLayout: @Composable () -> Unit = {
        HowToProveYourIdentityCard(
            onClick = {}
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> = applyLightDarkWelshAndFontScale()
    }
}
