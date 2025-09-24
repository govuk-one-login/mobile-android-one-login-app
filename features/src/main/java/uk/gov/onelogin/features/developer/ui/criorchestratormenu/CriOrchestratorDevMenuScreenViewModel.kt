package uk.gov.onelogin.features.developer.ui.criorchestratormenu

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

@HiltViewModel
class CriOrchestratorDevMenuScreenViewModel @Inject constructor(
    val criOrchestratorSdk: CriOrchestratorSdk
) : ViewModel() {
    fun set() {
        criOrchestratorSdk.appGraph
    }
}
