package uk.gov.onelogin.features.signout.ui.success

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
class SignOutSuccessAnalyticsViewModel @Inject constructor(
    @ApplicationContext
    context: Context,
    private val analytics: AnalyticsLogger
) : ViewModel() {
    private val walletScreenEvent = makeWalletScreenEvent(context)
    private val noWalletScreenEvent = makeNoWalletScreenEvent(context)
    private val primaryButtonEvent = makePrimaryEvent(context)
    private val makeBackEvent = makeBackPressed(context)

    fun trackWalletCopyScreen(walletEnabled: Boolean) {
        if (walletEnabled) {
            analytics.logEventV3Dot1(walletScreenEvent)
        } else {
            analytics.logEventV3Dot1(noWalletScreenEvent)
        }
    }

    fun trackPrimaryButton() {
        analytics.logEventV3Dot1(primaryButtonEvent)
    }

    fun trackBackPress() {
        analytics.logEventV3Dot1(makeBackEvent)
    }

    companion object {
        fun makeWalletScreenEvent(context: Context) = with(context) {
            ViewEvent.Screen(
                name = getEnglishString(R.string.app_signOutTitle),
                id = getEnglishString(R.string.sign_out_success_wallet_page_id),
                params = requiredParams
            )
        }

        fun makeNoWalletScreenEvent(context: Context) = with(context) {
            ViewEvent.Screen(
                name = getEnglishString(R.string.app_signOutTitle),
                id = getEnglishString(R.string.sign_out_success_no_wallet_page_id),
                params = requiredParams
            )
        }

        fun makePrimaryEvent(context: Context) = with(context) {
            TrackEvent.Button(
                text = getEnglishString(R.string.app_signOutSuccessButton),
                params = requiredParams
            )
        }

        fun makeBackPressed(context: Context) = with(context) {
            TrackEvent.Icon(
                text = getEnglishString(R.string.system_backButton),
                params =
                RequiredParameters(
                    taxonomyLevel2 = TaxonomyLevel2.ACCOUNT,
                    taxonomyLevel3 = TaxonomyLevel3.SIGN_OUT
                )
            )
        }

        private val requiredParams = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
            taxonomyLevel3 = TaxonomyLevel3.SIGN_OUT
        )
    }
}
