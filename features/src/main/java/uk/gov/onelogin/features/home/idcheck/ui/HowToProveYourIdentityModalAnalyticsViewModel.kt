package uk.gov.onelogin.features.home.idcheck.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.onelogin.features.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.ViewEvent
import javax.inject.Inject

@HiltViewModel
class HowToProveYourIdentityModalAnalyticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {
    fun trackScreen() {
        val event = ViewEvent.Screen(
            name = context.getEnglishString(R.string.app_proveYourIdentityGuidanceTitle),
            id = context.getEnglishString(R.string.prove_your_identity_guidance_page_id),
            params = RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.HOME,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED,
            )
        )

        analyticsLogger.logEventV3Dot1(event)
    }
}
