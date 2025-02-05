package uk.gov.onelogin.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.features.CriCardFeatureFlag
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository

@HiltViewModel
@Suppress("LongParameterList")
class HomeScreenViewModel @Inject constructor(
    val httpClient: GenericHttpClient,
    private val featureFlag: FeatureFlags,
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    val uiCardEnabled = MutableStateFlow(featureFlag[CriCardFeatureFlag.ENABLED])

    fun openDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun getUiCardFlagState() {
        uiCardEnabled.value = featureFlag[CriCardFeatureFlag.ENABLED]
    }
}
