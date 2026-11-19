package uk.gov.onelogin.libraries.mockwalletsdk.unit

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.core.test.screenshot.BaseScreenshotTest
import uk.gov.onelogin.libraries.mockwalletsdk.MockWalletPreview

@RunWith(Parameterized::class)
class MockWalletScreenshotTest(nightMode: NightMode) : BaseScreenshotTest(nightMode) {

    override val generateComposeLayout: @Composable () -> Unit = {
        MockWalletPreview()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> = arrayListOf(
            arrayOf(NOTNIGHT),
            arrayOf(NIGHT),
        )
    }
}
