package uk.gov.onelogin.features.signout.ui

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.signout.domain.SignOutUIState

@RunWith(Parameterized::class)
class SignOutScreenshotTest(
    signOutUIState: SignOutUIState,
    nightMode: NightMode
) : BaseScreenshotTest(nightMode) {
    override val generateComposeLayout: @Composable () -> Unit = {
        SignOutBody(
            uiState = signOutUIState,
            onPrimary = {},
            onClose = {},
            onBack = {}
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun values(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(SignOutUIState.NoWallet, NOTNIGHT),
                arrayOf(SignOutUIState.NoWallet, NIGHT),
                arrayOf(SignOutUIState.Wallet, NOTNIGHT),
                arrayOf(SignOutUIState.Wallet, NIGHT)
            )
        }
    }
}
