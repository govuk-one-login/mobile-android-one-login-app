package uk.gov.onelogin.ui.wallet

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.wallet.core.ui.theme.WalletTheme
import uk.gov.android.wallet.sdk.WalletApp

@Composable
@Preview
fun WalletScreen() {
    WalletTheme {
        WalletApp()
    }
}
