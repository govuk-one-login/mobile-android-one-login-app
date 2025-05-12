package uk.gov.onelogin.features.developer.ui.criorchestratormenu

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import javax.inject.Inject

@HiltViewModel
class CriOrchestratorDevMenuScreenViewModel
    @Inject
    constructor(
        val criOrchestratorSdk: CriOrchestratorSdk
    ) : ViewModel() {
        fun set() {
            criOrchestratorSdk.component
        }
    }
