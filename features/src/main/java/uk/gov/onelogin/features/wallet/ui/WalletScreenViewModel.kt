package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.wallet.sdk.WalletSdk
import javax.inject.Inject

@HiltViewModel
class WalletScreenViewModel
    @Inject
    constructor(
        val walletSdk: WalletSdk,
    ) : ViewModel()
