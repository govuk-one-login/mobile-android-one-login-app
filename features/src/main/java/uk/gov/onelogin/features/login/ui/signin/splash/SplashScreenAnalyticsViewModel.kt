package uk.gov.onelogin.features.login.ui.signin.splash

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
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
import javax.inject.Inject

@HiltViewModel
class SplashScreenAnalyticsViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val analyticsLogger: AnalyticsLogger,
    ) : ViewModel() {
        private val unlockBtnEvent = makeButtonEvent(context)

        fun trackSplashScreen(
            context: Context,
            isLocked: Boolean,
        ) {
            val event = makeScreenEvent(context, isLocked)
            analyticsLogger.logEventV3Dot1(event)
        }

        fun trackUnlockButton() {
            analyticsLogger.logEventV3Dot1(unlockBtnEvent)
        }

        fun trackBackButton(context: Context) {
            analyticsLogger.logEventV3Dot1(makeBackEvent(context))
        }

        companion object {
            private val requiredParameters =
                RequiredParameters(
                    taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                    taxonomyLevel3 = TaxonomyLevel3.UNDEFINED,
                )

            internal fun makeScreenEvent(
                context: Context,
                isLocked: Boolean,
            ) = with(context) {
                val correctDetails = getCorrectDetails(isLocked)

                ViewEvent.Screen(
                    name = getEnglishString(correctDetails.first),
                    id = getEnglishString(correctDetails.second),
                    params = requiredParameters,
                )
            }

            internal fun makeButtonEvent(context: Context) =
                with(context) {
                    TrackEvent.Button(
                        text = getEnglishString(R.string.app_unlockButton),
                        params = requiredParameters,
                    )
                }

            internal fun makeBackEvent(context: Context) =
                with(context) {
                    TrackEvent.Icon(
                        text = getEnglishString(R.string.system_backButton),
                        params = requiredParameters,
                    )
                }

            private fun getCorrectDetails(isLocked: Boolean): Pair<Int, Int> =
                if (isLocked) {
                    Pair(
                        R.string.app_splashScreenUnlockAnalyticsScreenName,
                        R.string.splash_unlock_screen_page_id,
                    )
                } else {
                    Pair(
                        R.string.app_splashScreenAnalyticsScreenName,
                        R.string.splash_screen_page_id,
                    )
                }
        }
    }
