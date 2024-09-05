package uk.gov.onelogin.optin.domain.source

import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState

class FakeOptInLocalSource: OptInLocalSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun getState(): AnalyticsOptInState {
        return currentState
    }

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
