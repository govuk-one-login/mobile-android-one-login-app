package uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway

import android.content.Context
import android.content.Intent
import uk.gov.onelogin.MainActivity

/**
 * A container for navigation model builders
 */
object ConfirmIdentityAnotherWayBuilderImpl : ConfirmIdentityAnotherWayBuilder {
    /**
     * Helper for creating [ConfirmIdentityAnotherWayNavModel], setting some standard arguments
     *
     * @param context App context
     * @param analyticsScreenName Calling screen's analytics screen name
     * @param section Calling screen's section
     * @param webHandbackUrl Abort URL for MAM journeys
     * @param isSdkActivity True when the caller of this function is within the ID Check SDK space.
     * Passed in as a navigation argument for the Confirm Another Way journey segment, the value
     * affects whether the Fragment performs further navigation or if it finishes the activity with
     * a result.
     */
    override fun buildNavModel(
        context: Context,
        analyticsScreenName: String,
        section: String,
        webHandbackUrl: String,
        isSdkActivity: Boolean,
    ): ConfirmIdentityAnotherWayNavModel =
        ConfirmIdentityAnotherWayNavModel(
            analyticsScreenName = analyticsScreenName,
            section = section,
            fullAppName = context.getString(uk.gov.android.onelogin.core.R.string.app_name),
            title =
                context.getString(uk.gov.android.onelogin.core.R.string.app_cri_orchestrator_placeholder),
            content =
                context.getString(uk.gov.android.onelogin.core.R.string.app_cri_orchestrator_placeholder),
            webHandbackUrl = webHandbackUrl,
            isSdkActivity = isSdkActivity,
        )

    /**
     * Helper for creating [ConfirmationAbortedNavModel], setting some standard arguments
     *
     * @param context App context
     * @param section Calling screen's section
     */
    override fun buildAbortNavModel(
        context: Context,
        section: String,
    ): ConfirmationAbortedNavModel =
        ConfirmationAbortedNavModel(
            section = section,
            additionalInfoText =
                context.getString(
                    uk.gov.android.onelogin.core.R.string.app_cri_orchestrator_placeholder
                ),
            substringToColour =
                context.getString(
                    uk.gov.android.onelogin.core.R.string.app_cri_orchestrator_placeholder
                ),
            intentToStart = Intent(context, MainActivity::class.java),
        )
}
