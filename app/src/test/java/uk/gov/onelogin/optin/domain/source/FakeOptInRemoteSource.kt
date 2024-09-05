package uk.gov.onelogin.optin.domain.source

import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState

class FakeOptInRemoteSource: OptInRemoteSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
