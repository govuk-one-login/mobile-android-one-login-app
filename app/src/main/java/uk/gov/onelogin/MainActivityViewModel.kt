package uk.gov.onelogin

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.wallet.core.issuer.verify.VerifyCredentialIssuerImpl.Companion.OID_QUERY_PARAM
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@HiltViewModel
@Suppress("LongParameterList")
class MainActivityViewModel @Inject constructor(
    private val analyticsOptInRepo: AnalyticsOptInRepository,
    private val walletRepository: WalletRepository,
    private val features: FeatureFlags,
    autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel(), DefaultLifecycleObserver {

    init {
        viewModelScope.launch {
            autoInitialiseSecureStore.initialise()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        viewModelScope.launch {
            analyticsOptInRepo.synchronise()
        }
    }

    fun handleIntent(intent: Intent?) {
        val walletEnabled = features[WalletFeatureFlag.ENABLED]
        if (intent?.action == ACTION_VIEW && intent.data != null && walletEnabled) {
            walletRepository.addDeepLinkPath(intent.data?.path)
            intent.data?.getQueryParameter(OID_QUERY_PARAM)?.let {
                walletRepository.addCredential(it)
            }
        }
    }
}
