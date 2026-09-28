package uk.gov.onelogin.features.optin.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.logging.api.analytics.logging.AnalyticsLogger as AnalyticsLoggerV1
import uk.gov.logging.api.analytics.logging.v3.AnalyticsLogger
import uk.gov.onelogin.features.optin.data.AnalyticsOptInState
import uk.gov.onelogin.features.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.features.optin.ui.IODispatcherQualifier
import javax.inject.Inject

class FirebaseAnalyticsOptInSource
    @Inject
    constructor(
        private val analyticsV1: AnalyticsLoggerV1,
        private val analytics: AnalyticsLogger,
        @param:IODispatcherQualifier
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OptInRemoteSource {
        override suspend fun update(state: AnalyticsOptInState) {
            withContext(dispatcher) {
                when (state) {
                    AnalyticsOptInState.No, AnalyticsOptInState.None -> {
                        analytics.setEnabled(false)
                        analyticsV1.setEnabled(false)
                    }

                    AnalyticsOptInState.Yes -> {
                        analytics.setEnabled(true)
                        analyticsV1.setEnabled(true)
                    }
                }
            }
        }
    }
