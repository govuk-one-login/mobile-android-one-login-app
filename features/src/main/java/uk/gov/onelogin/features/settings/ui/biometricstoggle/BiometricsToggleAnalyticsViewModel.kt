package uk.gov.onelogin.features.settings.ui.biometricstoggle

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.analytics.parameters.data.Type
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
import javax.inject.Inject

@HiltViewModel
class BiometricsToggleAnalyticsViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val analyticsLogger: AnalyticsLogger,
    ) : ViewModel() {
        private val walletCopyViewEvent = makeWalletCopyViewEvent(context)
        private val noWalletCopyViewEvent = makeNoWalletCopyViewEvent(context)
        private val backIconEvent = makeBackButtonIconEvent(context)
        private val backEvent = makeBackEvent(context)

        fun trackWalletCopyView() {
            analyticsLogger.logEventV3Dot1(walletCopyViewEvent)
        }

        fun trackNoWalletCopyView() {
            analyticsLogger.logEventV3Dot1(noWalletCopyViewEvent)
        }

        fun trackBackButton() {
            analyticsLogger.logEventV3Dot1(backEvent)
        }

        fun trackIconBackButton() {
            analyticsLogger.logEventV3Dot1(backIconEvent)
        }

        fun trackToggleEvent(value: Boolean) {
            analyticsLogger.logEventV3Dot1(makeToggleEventFormEvent(context, value))
        }

        companion object {
            internal fun makeWalletCopyViewEvent(context: Context) =
                with(context) {
                    ViewEvent.Screen(
                        name = getEnglishString(R.string.app_biometricsToggleTitle),
                        id = getEnglishString(R.string.biometrics_toggle_wallet_id),
                        params = requiredParams,
                    )
                }

            internal fun makeNoWalletCopyViewEvent(context: Context) =
                with(context) {
                    ViewEvent.Screen(
                        name = getEnglishString(R.string.app_biometricsToggleTitle),
                        id = getEnglishString(R.string.biometrics_toggle_no_wallet_id),
                        params = requiredParams,
                    )
                }

            fun makeBackButtonIconEvent(context: Context) =
                with(context) {
                    TrackEvent.Icon(
                        text = getEnglishString(R.string.app_back_icon),
                        params = requiredParams,
                    )
                }

            internal fun makeBackEvent(context: Context) =
                with(context) {
                    TrackEvent.Button(
                        text = getEnglishString(R.string.system_backButton),
                        params = requiredParams,
                    )
                }

            internal fun makeToggleEventFormEvent(
                context: Context,
                value: Boolean,
            ) = with(context) {
                TrackEvent.Form(
                    text = getEnglishString(R.string.app_biometricsToggleLabel),
                    response = value.toString(),
                    params = requiredParams,
                    type = Type.Toggle,
                )
            }

            private val requiredParams =
                RequiredParameters(
                    taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
                    taxonomyLevel3 = TaxonomyLevel3.BIOMETRICS_TOGGLE,
                )
        }
    }
