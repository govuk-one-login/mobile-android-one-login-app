package uk.gov.onelogin.features.settings.ui.ossl

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent

@HiltViewModel
class OsslAnalyticsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val screenEvent = makeScreenEvent(context)
    private val backButtonEvent = makeBackButtonEvent(context)
    private val backIconEvent = makeBackIconEvent(context)

    fun trackScreen() {
        analyticsLogger.logEventV3Dot1(screenEvent)
    }

    fun trackLink(licenseTitle: String, url: String) {
        analyticsLogger.logEventV3Dot1(
            makeLinkEvent(
                context,
                url,
                licenseTitle
            )
        )
    }

    fun trackBackIcon() {
        analyticsLogger.logEventV3Dot1(backIconEvent)
    }

    fun trackBackButton() {
        analyticsLogger.logEventV3Dot1(backButtonEvent)
    }

    companion object {
        internal fun makeScreenEvent(context: Context) = with(context) {
            ViewEvent.Screen(
                name = getEnglishString(R.string.app_osslTitle),
                id = getEnglishString(R.string.ossl_page_id),
                params = requiredParams
            )
        }

        internal fun makeLinkEvent(
            context: Context,
            url: String,
            title: String
        ) = with(context) {
            TrackEvent.Link(
                isExternal = true,
                domain = url,
                text = title,
                params = requiredParams
            )
        }

        internal fun makeBackButtonEvent(context: Context) = with(context) {
            TrackEvent.Icon(
                text = getEnglishString(R.string.system_backButton),
                params = requiredParams
            )
        }

        internal fun makeBackIconEvent(context: Context) = with(context) {
            TrackEvent.Icon(
                text = getEnglishString(R.string.system_backIcon),
                params = requiredParams
            )
        }

        private val requiredParams = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
            taxonomyLevel3 = TaxonomyLevel3.LICENCES
        )
    }
}
