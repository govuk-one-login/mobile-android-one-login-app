package uk.gov.onelogin.libraries.mocknetwork.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

@HiltViewModel
class MockNetworkViewModel
@Inject
constructor(private val config: MockNetworkConfig) :
    ViewModel() {
    val scenarios: List<MockNetworkScenario> = config.scenarios
    val scenario: StateFlow<MockNetworkScenario> = config.scenario

    fun selectScenario(scenario: MockNetworkScenario) = config.setScenario(scenario)
}
