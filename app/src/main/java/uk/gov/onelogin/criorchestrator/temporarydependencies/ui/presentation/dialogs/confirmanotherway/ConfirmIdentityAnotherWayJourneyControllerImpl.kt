package uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway

import android.content.ActivityNotFoundException
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.idcheck.repositories.api.webhandover.WebHandoverSession
import uk.gov.idcheck.ui.presentation.navigation.DirectionsLauncher
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.Logger
import uk.gov.onelogin.idcheck.sdk.NavIdCheckSdkDirections
import uk.gov.onelogin.idcheck.sdk.R

class ConfirmIdentityAnotherWayJourneyControllerImpl
@Inject
constructor(
    @ApplicationContext
    private val context: Context,
    private val launcher: DirectionsLauncher,
    private val reader: WebHandoverSession.Reader,
    private val logger: Logger,
    private val builder: ConfirmIdentityAnotherWayBuilder
) : ConfirmIdentityAnotherWayJourneyController,
    LogTagProvider {
    override fun navigateToConfirmAnotherWay(
        isSdkActivity: Boolean,
        screenName: String,
        section: String
    ) {
        val navigationModel =
            builder.buildNavModel(
                context = context,
                analyticsScreenName = screenName,
                section = section,
                webHandbackUrl = getWebUrl(),
                isSdkActivity = isSdkActivity
            )

        val abortModel =
            builder.buildAbortNavModel(
                context = context,
                section = section
            )

        val directions =
            NavIdCheckSdkDirections.globalConfirmAnotherWay(
                model = navigationModel,
                abortedModel = abortModel
            )
        try {
            launcher.launch(directions)
        } catch (exception: ActivityNotFoundException) {
            logger.error(
                tag,
                "Couldn't navigate to confirm ID another way! Navigating within SDK: " +
                    isSdkActivity,
                exception
            )
        }
    }

    private fun getWebUrl(): String {
        val authSessionID = reader.getSessionId()
        val baseUrl = context.getString(R.string.webBaseUrl)
        return context.getString(
            R.string.webHandbackLink,
            baseUrl,
            authSessionID
        )
    }
}
