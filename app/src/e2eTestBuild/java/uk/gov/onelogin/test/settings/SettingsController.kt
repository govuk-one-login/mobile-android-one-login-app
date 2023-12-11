package uk.gov.onelogin.test.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.runBlocking
import uk.gov.onelogin.R
import uk.gov.onelogin.login.SuccessfulLoginTest.Companion.WAIT_FOR_OBJECT_TIMEOUT

class SettingsController (
    private val context: Context,
    private val device: UiDevice
) {
    fun enableOpenLinksByDefault() {
        openSettings()
        selectOpenByDefault()
        addLinks()
        device.pressHome()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)

        device.waitForIdle()
        if (!device.hasObject(By.text("App info"))) {
            throw Error("Not managed to open the settings page for package: ${context.packageName}")
        }
    }

    private fun selectOpenByDefault() {
        when (VERSION.SDK_INT) {
            VERSION_CODES.Q,
            VERSION_CODES.R -> selectOpenByDefaultApi29()
            VERSION_CODES.S,
            VERSION_CODES.S_V2,
            VERSION_CODES.TIRAMISU -> selectOpenByDefaultApi33()
        }
    }

    private fun addLinks() {
        when (VERSION.SDK_INT) {
            VERSION_CODES.Q -> addLinksApi29()
            VERSION_CODES.R -> addLinksApi30()
            VERSION_CODES.S,
            VERSION_CODES.S_V2,
            VERSION_CODES.TIRAMISU -> addLinksApi33()
        }
    }

    private fun selectOpenByDefaultApi29() {
        val settingsPage = UiScrollable(UiSelector().scrollable(true))
        val advancedSelector = UiSelector().childSelector(
            UiSelector().text("Advanced")
        )

        try {
            settingsPage.scrollIntoView(advancedSelector)
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        try {
            device.findObject(By.text("Advanced"))?.click()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        selectOpenByDefaultApi33()
    }

    private fun selectOpenByDefaultApi33() {
        val settingsPage = UiScrollable(UiSelector().scrollable(true))
        val openByDefaultSelector = UiSelector().childSelector(
            UiSelector().text("Open by default")
        )

        try {
            settingsPage.scrollIntoView(openByDefaultSelector)
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        try {
            device.findObject(By.text("Open by default"))?.click()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun addLinksApi29() {
        val openByDefaultPage = UiScrollable(UiSelector().scrollable(true))
        val openSupportedLinksSelector = UiSelector().childSelector(
            UiSelector().text("Open supported links")
        )

        try {
            openByDefaultPage.scrollIntoView(openSupportedLinksSelector)
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        try {
            device.findObject(By.text("Open supported links")).click()
            device.wait(Until.findObject(By.text("Open in this app")), WAIT_FOR_OBJECT_TIMEOUT).click()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun addLinksApi30() {
        val openByDefaultPage = UiScrollable(UiSelector().scrollable(true))
        val openSupportedLinksSelector = UiSelector().childSelector(
            UiSelector().text("Open supported links")
        )

        try {
            openByDefaultPage.scrollIntoView(openSupportedLinksSelector)
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        try {
            device.findObject(By.text("Open supported links")).click()
            device.wait(
                Until.findObject(By.text("Allow app to open supported links")),
                WAIT_FOR_OBJECT_TIMEOUT
            ).click()
            device.pressBack()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun addLinksApi33() {
        val openByDefaultPage = UiScrollable(UiSelector().scrollable(true))
        val addLinkSelector = UiSelector().childSelector(
            UiSelector().text("Add link")
        )

        try {
            openByDefaultPage.scrollIntoView(addLinkSelector)
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        try {
            if (device.findObject(addLinkSelector).isEnabled) {
                device.findObject(By.text("Add link")).click()
                device.waitForIdle()
                runBlocking {
                    Thread.sleep(5000)
                }
                val webBaseHost = context.resources.getString(R.string.webBaseHost)
                val selector = By.text(webBaseHost)
                var element = device.findObject(selector)
                var count = 0
                while (element == null && count < 10) {
                    runBlocking {
                        Thread.sleep(100)
                    }
                    element = device.findObject(selector)
                    count++
                }
                element?.click() ?: throw Error("Couldn't find $webBaseHost in the links")
                device.findObject(By.text("Add")).click()
            }
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }
}
