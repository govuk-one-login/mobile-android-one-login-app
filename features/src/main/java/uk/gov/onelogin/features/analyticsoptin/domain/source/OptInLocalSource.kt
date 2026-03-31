package uk.gov.onelogin.features.analyticsoptin.domain.source

import uk.gov.onelogin.features.analyticsoptin.data.AnalyticsOptInState

interface OptInLocalSource {
    suspend fun getState(): AnalyticsOptInState

    suspend fun update(state: AnalyticsOptInState)
}
