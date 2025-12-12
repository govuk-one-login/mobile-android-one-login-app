package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletSdk

@HiltViewModel
class WalletScreenViewModel @Inject constructor(
    val walletSdk: WalletSdk
) : ViewModel()
