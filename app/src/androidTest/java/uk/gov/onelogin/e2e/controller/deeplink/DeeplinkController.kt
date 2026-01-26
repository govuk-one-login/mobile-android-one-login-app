package uk.gov.onelogin.e2e.controller.deeplink

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import uk.gov.onelogin.e2e.JourneyController

object DeeplinkController {
    fun testDeeplink(
        context: Context,
        deeplinkFilePath: String,
    ) = JourneyController { controller ->
        controller.apply {
            launchDeeplink(
                context,
                PACKAGE_NAME,
                getDeeplinkUrl(deeplinkFilePath),
                LAUNCH_TIMEOUT,
            )
        }
    }

    private fun getDeeplinkUrl(path: String): String =
        InstrumentationRegistry
            .getInstrumentation()
            .context
            .classLoader
            .getResource(path)
            .readText()

    private const val LAUNCH_TIMEOUT = 5000L
    private const val PACKAGE_NAME = "uk.gov.onelogin.build"
}
