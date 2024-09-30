package uk.gov.onelogin.optin.data

import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.optin.ui.IODispatcherQualifier

class FirebaseAnalyticsOptInSource @Inject constructor(
    private val analytics: FirebaseAnalytics,
    @IODispatcherQualifier
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OptInRemoteSource {

    override suspend fun update(state: AnalyticsOptInState) {
        withContext(dispatcher) {
            when (state) {
                AnalyticsOptInState.No,
                AnalyticsOptInState.None -> analytics.setAnalyticsCollectionEnabled(false)
                AnalyticsOptInState.Yes -> analytics.setAnalyticsCollectionEnabled(true)
            }
        }
    }
}
