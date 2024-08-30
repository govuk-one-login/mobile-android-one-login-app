package uk.gov.onelogin.e2e.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import uk.gov.android.onelogin.BuildConfig
import uk.gov.android.onelogin.R

class SettingsController(
    private val context: Context,
    private val phoneController: PhoneController
) {
    private val tag = this::class.java.simpleName
    fun enableOpenLinksByDefault() {
        openSettings()
        selectOpenByDefault()
        addLinks()
        phoneController.pressBack()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)

        phoneController.waitUntilIdle()
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
            Log.e(tag, e.message, e)
        }

        try {
            phoneController.optionalClick(
                selectors = arrayOf(
                    By.text("Advanced") to "Advanced"
                )
            )
        } catch (e: UiObjectNotFoundException) {
            Log.e(tag, e.message, e)
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
            Log.e(tag, e.message, e)
        }

        try {
            phoneController.click(
                selectors = arrayOf(
                    By.text("Open by default") to "Open by default"
                )
            )
        } catch (e: UiObjectNotFoundException) {
            Log.e(tag, e.message, e)
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
            Log.e(tag, e.message, e)
        }

        try {
            phoneController.click(
                selectors = arrayOf(
                    By.text("Open supported links") to "Open supported links",
                    By.text("Open in this app") to "Open in this app"
                )
            )
        } catch (e: UiObjectNotFoundException) {
            Log.e(tag, e.message, e)
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
            Log.e(tag, e.message, e)
        }

        try {
            phoneController.click(
                selectors = arrayOf(
                    By.text("Open supported links") to "Open supported links",
                    By.text("Allow app to open supported links")
                        to "Allow app to open supported links"
                )
            )
            phoneController.pressBack()
        } catch (e: UiObjectNotFoundException) {
            Log.e(tag, e.message, e)
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
            Log.e(tag, e.message, e)
        }

        try {
            if (phoneController.isEnabled(addLinkSelector)) {
                phoneController.click(
                    selectors = arrayOf(
                        By.text("Add link") to "Add link",
                        By.text(context.resources.getString(R.string.webBaseUrl))
                            to "Supported link",
                        By.text("Add") to "Add"
                    )
                )
            }
        } catch (e: UiObjectNotFoundException) {
            Log.e(tag, e.message, e)
        }
    }
}
