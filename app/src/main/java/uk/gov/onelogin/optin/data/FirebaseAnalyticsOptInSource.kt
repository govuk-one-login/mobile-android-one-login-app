package uk.gov.onelogin.optin.data

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.optin.ui.IODispatcherQualifier

class FirebaseAnalyticsOptInSource @Inject constructor(
    private val analytics: AnalyticsLogger,
    @IODispatcherQualifier
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OptInRemoteSource {

    override suspend fun update(state: AnalyticsOptInState) {
        withContext(dispatcher) {
            when (state) {
                AnalyticsOptInState.No,
                AnalyticsOptInState.None -> analytics.setEnabled(false)
                AnalyticsOptInState.Yes -> analytics.setEnabled(true)
            }
        }
    }
}
