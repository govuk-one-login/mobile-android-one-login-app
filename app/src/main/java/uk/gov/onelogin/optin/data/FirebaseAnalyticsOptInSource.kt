package uk.gov.onelogin.optin.data

import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import javax.inject.Inject

class FirebaseAnalyticsOptInSource @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OptInRemoteSource {

    override suspend fun update(state: AnalyticsOptInState) {
        withContext(dispatcher) {
            when (state) {
                AnalyticsOptInState.No -> analytics.setAnalyticsCollectionEnabled(false)
                AnalyticsOptInState.Yes -> analytics.setAnalyticsCollectionEnabled(true)
                AnalyticsOptInState.None -> Unit // protected from happening
            }
        }
    }
}
