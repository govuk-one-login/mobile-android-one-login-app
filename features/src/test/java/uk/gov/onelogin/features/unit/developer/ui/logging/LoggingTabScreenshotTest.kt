package uk.gov.onelogin.features.unit.developer.ui.logging

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.developer.ui.logging.LoggingTabScreenPreview

@RunWith(Parameterized::class)
class LoggingTabScreenshotTest(
    nightMode: NightMode,
) : BaseScreenshotTest(nightMode) {
    override val generateComposeLayout: @Composable () -> Unit = {
        LoggingTabScreenPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> =
            arrayListOf(
                arrayOf(NOTNIGHT),
                arrayOf(NIGHT),
            )
    }
}
