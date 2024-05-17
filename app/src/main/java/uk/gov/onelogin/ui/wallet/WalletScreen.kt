package uk.gov.onelogin.ui.wallet

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.onelogin.R
import uk.gov.android.ui.pages.TitledPage
import uk.gov.android.ui.pages.TitledPageParameters

@Composable
fun WalletScreen() {
    TitledPage(parameters = TitledPageParameters(R.string.app_walletTitle))
}

@Composable
@Preview
private fun Preview() {
    WalletScreen()
}
