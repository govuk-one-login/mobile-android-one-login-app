package uk.gov.onelogin.features.developer.ui.securestore

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecureStoreScreenViewModel
    @Inject
    constructor(
        private val secureStoreRepository: SecureStoreDevOptionsRepository,
    ) : ViewModel() {
        private val _overrideWallet =
            MutableStateFlow(
                secureStoreRepository.isWalletDeleteOverride(),
            )
        private val _enableDeletionFail =
            MutableStateFlow(
                secureStoreRepository.isLocalDataDeleteFailEnabled(),
            )
        val overrideWallet: StateFlow<Boolean>
            get() = _overrideWallet.asStateFlow()
        val enableDeletionFail: StateFlow<Boolean>
            get() = _enableDeletionFail.asStateFlow()

        fun setOverride(override: Boolean) {
            secureStoreRepository.overrideWalletDelete(override)
            _overrideWallet.value = override
        }

        fun setDeletionFail(enableFail: Boolean) {
            secureStoreRepository.enableLocalDataDeleteFail(enableFail)
            _enableDeletionFail.value = enableFail
        }
    }
