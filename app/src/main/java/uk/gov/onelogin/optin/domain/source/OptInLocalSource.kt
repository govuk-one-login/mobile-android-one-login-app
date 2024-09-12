package uk.gov.onelogin.optin.domain.source

import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState

interface OptInLocalSource {
    suspend fun getState(): AnalyticsOptInState
    suspend fun update(state: AnalyticsOptInState)
}
