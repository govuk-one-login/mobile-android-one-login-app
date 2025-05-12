package uk.gov.onelogin.features.optin.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.features.optin.data.AnalyticsOptInState
import uk.gov.onelogin.features.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.features.optin.ui.IODispatcherQualifier
import javax.inject.Inject

class FirebaseAnalyticsOptInSource
    @Inject
    constructor(
        private val analytics: AnalyticsLogger,
        @IODispatcherQualifier
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : OptInRemoteSource {
        override suspend fun update(state: AnalyticsOptInState) {
            withContext(dispatcher) {
                when (state) {
                    AnalyticsOptInState.No, AnalyticsOptInState.None -> analytics.setEnabled(false)

                    AnalyticsOptInState.Yes -> analytics.setEnabled(true)
                }
            }
        }
    }
