package uk.gov.onelogin.login.ui.biooptin

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent

@HiltViewModel
class BioOptInAnalyticsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val screenEvent = makeScreenEvent(context)
    private val biometricsBtnEvent = makeButtonEvent(context, R.string.app_enableBiometricsButton)
    private val passcodeBtnEvent = makeButtonEvent(
        context,
        R.string.app_enablePasscodeOrPatternButton
    )

    fun trackBioOptInScreen() {
        analyticsLogger.logEventV3Dot1(screenEvent)
    }

    fun trackBiometricsButton() {
        analyticsLogger.logEventV3Dot1(biometricsBtnEvent)
    }

    fun trackPasscodeButton() {
        analyticsLogger.logEventV3Dot1(passcodeBtnEvent)
    }

    private fun makeScreenEvent(context: Context) = with(context) {
        ViewEvent.Screen(
            name = getEnglishString(R.string.app_enableBiometricsTitle),
            id = getEnglishString(R.string.bio_opt_in_screen_page_id),
            params = RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
            )
        )
    }

    private fun makeButtonEvent(context: Context, text: Int) = with(context) {
        TrackEvent.Button(
            text = getEnglishString(text),
            params = RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
            )
        )
    }
}
