package uk.gov.onelogin.features.navigation.domain

import android.content.Intent
import androidx.core.net.toUri
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.navigation.domain.NavigationException
import uk.gov.onelogin.core.navigation.domain.WebNavigator
import uk.gov.onelogin.core.utils.ActivityProvider
import javax.inject.Inject

class WebNavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val logger: Logger,
) : WebNavigator, LogTagProvider {
    override fun openWebBrowser(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())

        val activity = activityProvider.getCurrentActivity()

        if (activity == null) {
            val message = "Navigation to web failed"
            val cause = NullPointerException("ActivityProvider.getCurrentActivity() was null")
            logger.error(message, NavigationException(message, cause))
            return
        }

        activity.startActivity(intent)
    }
}
