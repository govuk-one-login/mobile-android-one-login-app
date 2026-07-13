package uk.gov.onelogin.features.unit.settings.ui.ossl

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.LOCALE_CY
import uk.gov.onelogin.features.LOCALE_EN
import uk.gov.onelogin.features.settings.ui.ossl.OsslAboutLibrariesScreenPreview

@RunWith(Parameterized::class)
class OsslAboutLibrariesScreenshotTest(
    nightMode: NightMode,
    locale: String
) : BaseScreenshotTest(nightMode, locale) {
    override val generateComposeLayout: @Composable () -> Unit = {
        OsslAboutLibrariesScreenPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> =
            arrayListOf(
                arrayOf(NOTNIGHT, LOCALE_EN),
                arrayOf(NIGHT, LOCALE_EN)
            )
    }
}
