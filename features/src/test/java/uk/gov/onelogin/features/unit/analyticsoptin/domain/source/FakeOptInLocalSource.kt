package uk.gov.onelogin.features.unit.analyticsoptin.domain.source

import uk.gov.onelogin.features.analyticsoptin.data.AnalyticsOptInState
import uk.gov.onelogin.features.analyticsoptin.domain.source.OptInLocalSource

class FakeOptInLocalSource : OptInLocalSource {
    var currentState = AnalyticsOptInState.None

    override suspend fun getState(): AnalyticsOptInState = currentState

    override suspend fun update(state: AnalyticsOptInState) {
        currentState = state
    }
}
