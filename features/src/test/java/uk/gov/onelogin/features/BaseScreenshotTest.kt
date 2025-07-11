package uk.gov.onelogin.features

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode.SHRINK
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.Rule
import org.junit.Test
import uk.gov.android.ui.theme.m3.GdsTheme

/**
 * Base implementation for tests that utilise the Paparazzi testing library.
 *
 * Implementations of [BaseScreenshotTest] would at minimum override the [generateComposeLayout]
 * property to verify behaviour.
 *
 * Implementations may also use the [org.junit.runners.Parameterized] runner if requiring more than
 * one UI composition for screenshot purposes.
 *
 * @sample uk.gov.onelogin.features.error.ui.generic.GenericErrorScreenShotTest
 */
abstract class BaseScreenshotTest(
    nightMode: NightMode = NOTNIGHT,
    locale: String = LOCALE_EN
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig =
            DeviceConfig.PIXEL_6.copy(
                nightMode = nightMode,
                locale = locale
            ),
            renderingMode = SHRINK,
            showSystemUi = false
        )

    @Test
    fun testScreenshot() {
        paparazzi.snapshot {
            GdsTheme {
                generateComposeLayout()
            }
        }
    }

    protected abstract val generateComposeLayout: @Composable () -> Unit

    companion object {
        @JvmStatic
        fun <T : Any> applyNightMode(result: MutableList<Pair<T, NightMode>>): (
            T
        ) -> Unit =
            { parameters ->
                result.add(parameters to NOTNIGHT)
                result.add(parameters to NIGHT)
            }

        @JvmStatic
        fun applyNightModeAndLocale(): Iterable<Array<Any>> = arrayListOf(
            arrayOf(NOTNIGHT, LOCALE_EN),
            arrayOf(NIGHT, LOCALE_EN),
            arrayOf(NOTNIGHT, LOCALE_CY),
            arrayOf(NIGHT, LOCALE_CY)
        )
    }
}

const val LOCALE_EN = "en"
const val LOCALE_CY = "cy"
