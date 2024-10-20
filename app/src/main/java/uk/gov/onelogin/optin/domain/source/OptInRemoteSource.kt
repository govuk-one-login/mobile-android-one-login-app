package uk.gov.onelogin.optin.domain.source

import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState

fun interface OptInRemoteSource {
    suspend fun update(state: AnalyticsOptInState)
}
