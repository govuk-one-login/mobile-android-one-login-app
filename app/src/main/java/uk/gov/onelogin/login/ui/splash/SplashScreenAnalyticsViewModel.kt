package uk.gov.onelogin.login.ui.splash

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
import uk.gov.logging.api.v3dot1.model.ViewEvent

@HiltViewModel
class SplashScreenAnalyticsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val splashScreenViewEvent = makeScreenEvent(context)

    fun trackSplashScreen() {
        analyticsLogger.logEventV3Dot1(splashScreenViewEvent)
    }

    private fun makeScreenEvent(context: Context) = with(context) {
        ViewEvent.Screen(
            name = getEnglishString(R.string.app_splashScreenAnalyticsScreenName),
            id = getEnglishString(R.string.splash_screen_page_id),
            params = RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
            )
        )
    }
}
