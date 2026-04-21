package uk.gov.onelogin.features.unit.optin.domain.source

import uk.gov.onelogin.features.optin.data.AnalyticsOptInState
import uk.gov.onelogin.features.optin.domain.source.OptInRemoteSource

class FakeOptInRemoteSource : OptInRemoteSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
