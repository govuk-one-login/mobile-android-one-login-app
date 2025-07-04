package uk.gov.onelogin.features.signout.ui

import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import com.android.resources.NightMode.NIGHT
import com.android.resources.NightMode.NOTNIGHT
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import uk.gov.onelogin.features.BaseScreenshotTest
import uk.gov.onelogin.features.LOCALE_CY
import uk.gov.onelogin.features.LOCALE_EN
import uk.gov.onelogin.features.signout.domain.SignOutUIState

@RunWith(Parameterized::class)
class SignOutScreenshotTest(
    signOutUIState: SignOutUIState,
    nightMode: NightMode,
    locale: String
) : BaseScreenshotTest(nightMode, locale) {
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
                arrayOf(SignOutUIState.NoWallet, NOTNIGHT, LOCALE_EN),
                arrayOf(SignOutUIState.NoWallet, NIGHT, LOCALE_EN),
                arrayOf(SignOutUIState.Wallet, NOTNIGHT, LOCALE_EN),
                arrayOf(SignOutUIState.Wallet, NIGHT, LOCALE_EN),
                arrayOf(SignOutUIState.NoWallet, NOTNIGHT, LOCALE_CY),
                arrayOf(SignOutUIState.NoWallet, NIGHT, LOCALE_CY),
                arrayOf(SignOutUIState.Wallet, NOTNIGHT, LOCALE_CY),
                arrayOf(SignOutUIState.Wallet, NIGHT, LOCALE_CY)
            )
        }
    }
}
