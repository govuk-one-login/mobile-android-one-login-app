package uk.gov.onelogin.features.unit.analyticsoptin.domain.source

import uk.gov.onelogin.features.analyticsoptin.data.AnalyticsOptInState
import uk.gov.onelogin.features.analyticsoptin.domain.source.OptInRemoteSource

class FakeOptInRemoteSource : OptInRemoteSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
