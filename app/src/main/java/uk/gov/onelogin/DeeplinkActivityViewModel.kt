package uk.gov.onelogin

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.android.wallet.core.issuer.verify.VerifyCredentialIssuerImpl.Companion.OID_QUERY_PARAM
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.wallet.data.WalletRepository
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class DeeplinkActivityViewModel
    @Inject
    constructor(
        private val walletRepository: WalletRepository,
        private val walletSdk: WalletSdk,
    ) : ViewModel() {
        fun handleIntent(intent: Intent?) {
            if (intent?.action == ACTION_VIEW && intent.data != null) {
                intent.data?.getQueryParameter(OID_QUERY_PARAM)?.let {
                    viewModelScope.launch {
                        walletRepository.setWalletDeepLinkPathState(deepLink = true)
                        walletSdk.setDeeplink(deeplink = it)
                    }
                }
            }
        }
    }
