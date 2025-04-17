package uk.gov.onelogin.features.home.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.featureflags.data.CriOrchestratorFeatureFlag

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val featureFlag: FeatureFlags,
    private val navigator: Navigator,
    val criOrchestratorSdk: CriOrchestratorSdk
) : ViewModel() {
    private val _uiCardEnabled = MutableStateFlow(
        featureFlag[CriOrchestratorFeatureFlag.ENABLED]
    )
    val uiCardEnabled: StateFlow<Boolean> = _uiCardEnabled

    fun openDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun getUiCardFlagState() {
        _uiCardEnabled.value = featureFlag[CriOrchestratorFeatureFlag.ENABLED]
    }
}
