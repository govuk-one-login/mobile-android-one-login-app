package uk.gov.onelogin.features.error.ui.appintegrity

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.ViewEvent
import javax.inject.Inject

@HiltViewModel
class AppIntegrityErrorAnalyticsViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val analyticsLogger: AnalyticsLogger,
    ) : ViewModel() {
        private val screenEvent = makeScreenEvent(context)

        fun trackScreen() {
            analyticsLogger.logEventV3Dot1(screenEvent)
        }

        companion object {
            internal fun makeScreenEvent(context: Context) =
                with(context) {
                    ViewEvent.Error(
                        name = getEnglishString(R.string.app_appIntegrityErrorTitle),
                        id = getEnglishString(R.string.app_integrity_error_firebase_screen_id),
                        endpoint = "",
                        reason = getEnglishString(R.string.app_integrity_error_reason),
                        status = "",
                        params = requiredParams,
                    )
                }

            private val requiredParams =
                RequiredParameters(
                    taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                    taxonomyLevel3 = TaxonomyLevel3.UNDEFINED,
                )
        }
    }
