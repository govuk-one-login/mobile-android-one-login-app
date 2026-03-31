package uk.gov.onelogin.features.analyticsoptin.domain.source

import uk.gov.onelogin.features.analyticsoptin.data.AnalyticsOptInState

fun interface OptInRemoteSource {
    suspend fun update(state: AnalyticsOptInState)
}
