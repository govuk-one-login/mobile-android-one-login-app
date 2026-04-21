package uk.gov.onelogin.features.unit.optin.domain.source

import uk.gov.onelogin.features.optin.data.AnalyticsOptInState
import uk.gov.onelogin.features.optin.domain.source.OptInLocalSource

class FakeOptInLocalSource : OptInLocalSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun getState(): AnalyticsOptInState = currentState

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
