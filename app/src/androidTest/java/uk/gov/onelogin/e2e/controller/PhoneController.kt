package uk.gov.onelogin.e2e.controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.Surface
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import java.io.File
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Assert.assertTrue
import uk.gov.android.onelogin.BuildConfig
import uk.gov.onelogin.MainActivity

typealias BySelectorEntry = Pair<BySelector, String>

/**
 * Performs actions on an Android-powered device via the [device] class.
 *
 * This means that actions performed by this class are unaware of the internal workings of the
 * currently displayed apps.
 *
 * @param testNameRule Provides the test class and method names for screenshot purposes.
 * @param phoneActionTimeout The maximum amount of time in milliseconds that the [device] should
 * wait before a requested action is complete. Throws an exception when exceeding this timer.
 * @param postActionDelay The time in milliseconds for the current Thread using the
 * [PhoneController] should sleep after performing an action.
 * @param device The object used to interact with the Android-powered device.
 */
@Suppress("TooManyFunctions")
class PhoneController(
    private val testNameRule: TestCaseNameWatcher,
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    ),
    private val phoneActionTimeout: Long = 3000L,
    private val postActionDelay: Long = 1000L
) {

    /**
     * Tracks the number of screenshots taken with this instance of the [PhoneController].
     *
     * Internally increments with usage of the [screenshot] function due to it's usage in the
     * filename of the generated screenshot.
     */
    private var photoCount = 1

    /**
     * Waits until the device is idle before continuing within the test.
     *
     * Throws an exception if the time to wait is longer than [actionTimeoutOverride].
     *
     * @param actionTimeoutOverride The time to wait for the device to become idle. Defaults to
     * [phoneActionTimeout].
     */
    fun waitUntilIdle(actionTimeoutOverride: Long = phoneActionTimeout) {
        device.waitForIdle(actionTimeoutOverride)
        Thread.sleep(postActionDelay)
    }

    /**
     * Waits until the UI elements described by the [selectors] are no longer visible.
     *
     * Throws an exception if any provided [selectors] element remains visible for longer than the
     * [actionTimeoutOverride].
     *
     * @param actionTimeoutOverride The time to wait for a UI element to become disappear.
     * Defaults to [phoneActionTimeout].
     * @param selectors the array of [BySelector] objects that should be invisible. The array is
     * handled sequentially.
     */
    fun waitUntilGone(
        actionTimeoutOverride: Long = phoneActionTimeout,
        vararg selectors: BySelector
    ) = selectors.forEach { selector ->
        Assert.assertTrue(
            device.wait(
                Until.gone(selector),
                actionTimeoutOverride
            )
        )

        Thread.sleep(postActionDelay)
    }

    /**
     * Presses the hardware back button on the Device.
     */
    fun pressBack() {
        device.pressBack()
        Thread.sleep(postActionDelay)
    }

    /**
     * Presses the hardware home button on the Device.
     */
    fun pressHome() {
        device.pressHome()
        Thread.sleep(postActionDelay)
    }

    fun resetDevice() {
        device.executeShellCommand("adb shell pm clear uk.gov.onelogin.build")
    }

    /**
     * Taps the 'Allow' button that's shown on the Device.
     *
     * Calling this function is usually for when the Android system dialog appears for permissions.
     */
    fun allowPermission() = click(
        selectors = arrayOf(By.text("Allow") to "Clicking 'Allow' for the open permission dialog")
    )

    /**
     * Taps the 'Deny' button that's shown on the Device.
     *
     * Calling this function is usually for when the Android system dialog appears for permissions.
     */
    fun denyPermission() = click(
        selectors = arrayOf(By.text("Deny") to "Clicking 'Deny' for the open permission dialog")
    )

    /**
     * Taps UI elements defined by the [selectors] array.
     *
     * Waits for the visible UI element before tapping. Throws an exception if the element isn't
     * visible after the time defined by [actionTimeoutOverride].
     *
     * If the internally generated [androidx.test.uiautomator.UiObject2] becomes out of date, as
     * shown by catching a [StaleObjectException], the function is recursively called with the
     * failing UI element as the only parameter for [selectors].
     *
     * @param actionTimeoutOverride The time to wait for the UI element to become available to tap.
     * Defaults to [phoneActionTimeout].
     * @param selectors the array of [BySelector] objects to tap. The array sequentially handled.
     */
    @Suppress("TooGenericExceptionCaught")
    fun click(
        actionTimeoutOverride: Long = phoneActionTimeout,
        vararg selectors: BySelectorEntry
    ): Unit = selectors.forEach { selectorEntry: BySelectorEntry ->
        val selector = selectorEntry.first
        val selectorMessage = selectorEntry.second

        device.waitForIdle(actionTimeoutOverride)

        try {
            screenshot(selectorMessage)
            device.wait(
                Until.findObject(selector),
                actionTimeoutOverride
            ).click()

            Thread.sleep(postActionDelay)
        } catch (staleException: StaleObjectException) {
            println(
                "View for the selector \"$selectorMessage\" is out of date. Retrying... " +
                    "($staleException)"
            )
            click(actionTimeoutOverride, selectorEntry)
        } catch (exception: Exception) {
            val message = "Error found with \"$selectorMessage\": $exception"
            throw PhoneControllerException(message, exception)
        }
    }

    /**
     * Scrolls down the screen looking for UI elements defined by the [selectors] array before
     * tapping the found element
     *
     * Waits for the visible UI element before tapping. Throws an exception if the element isn't
     * visible after the time defined by [actionTimeoutOverride].
     *
     * If the internally generated [androidx.test.uiautomator.UiObject2] becomes out of date, as
     * shown by catching a [StaleObjectException], the function is recursively called with the
     * failing UI element as the only parameter for [selectors].
     *
     * @param actionTimeoutOverride The time to wait for the UI element to become available to tap.
     * Defaults to [phoneActionTimeout].
     * @param selectors the array of [BySelector] objects to tap. The array sequentially handled.
     */
    @Suppress("TooGenericExceptionCaught")
    fun scrollAndClick(
        actionTimeoutOverride: Long = phoneActionTimeout,
        vararg selectors: BySelectorEntry
    ): Unit = selectors.forEach { selectorEntry: BySelectorEntry ->
        val selector = selectorEntry.first
        val selectorMessage = selectorEntry.second

        device.waitForIdle(actionTimeoutOverride)

        try {
            screenshot(selectorMessage)

            device.findObject(
                By.scrollable(true)
            ).scroll(
                Direction.DOWN,
                100F
            )

            device.wait(
                Until.findObject(selector),
                actionTimeoutOverride
            ).click()

            Thread.sleep(postActionDelay)
        } catch (staleException: StaleObjectException) {
            println(
                "View for the selector \"$selectorMessage\" is out of date. Retrying... " +
                    "($staleException)"
            )
            click(actionTimeoutOverride, selectorEntry)
        } catch (exception: Exception) {
            val message = "Error found with \"$selectorMessage\": $exception"
            throw PhoneControllerException(message, exception)
        }
    }

    fun setText(
        actionTimeoutOverride: Long = phoneActionTimeout,
        textToEnter: String,
        selectorEntry: BySelectorEntry
    ) {
        val selector = selectorEntry.first
        val selectorMessage = selectorEntry.second

        device.waitForIdle(actionTimeoutOverride)

        try {
            screenshot(selectorMessage)
            device.wait(
                Until.findObject(selector),
                actionTimeoutOverride
            ).text = textToEnter

            Thread.sleep(postActionDelay)
        } catch (staleException: StaleObjectException) {
            println(
                "View for the selector \"$selectorMessage\" is out of date. Retrying... " +
                    "($staleException)"
            )
            setText(actionTimeoutOverride, textToEnter, selectorEntry)
        } catch (exception: Exception) {
            val message = "Error found with \"$selectorMessage\": $exception"
            throw PhoneControllerException(message, exception)
        }
    }

    /**
     * Taps UI elements defined by the [selectors] array.
     *
     * Internally defers to the [click] function, swallowing caught exceptions as the UI element
     * may not exist.
     *
     * @param actionTimeoutOverride The time to wait for the UI element to be available to tap.
     * Defaults to [phoneActionTimeout].
     * @param selectors the array of [BySelector] objects to tap. The array is sequentially handled.
     */
    @Suppress("TooGenericExceptionCaught")
    fun optionalClick(
        actionTimeoutOverride: Long = phoneActionTimeout,
        vararg selectors: BySelectorEntry
    ) = selectors.forEach { selectorEntry ->
        val selectorMessage = selectorEntry.second
        try {
            click(actionTimeoutOverride, selectorEntry)
        } catch (exception: Exception) {
            println(
                "Assertion error due to un-required step " +
                    "( $selectorMessage )!: $exception"
            )
        }
    }

    fun isEnabled(
        selector: UiSelector
    ): Boolean = device.findObject(selector).isEnabled

    /**
     * Uses the [UiDevice] constructor parameter to take a screenshot.
     *
     * Stores the image within
     * `/data/data/$appId/files/screenshots/$flavor/$buildType/phoneController`
     * on the device.
     *
     * Names the file `$currentStepNumber - $suffix.png`, so beware of passing in a folder within
     * the [suffix] parameter.
     *
     * @param suffix the filename after the programmatic prefix of total screenshots taken.
     *
     * @see uk.gov.documentchecking.utils.screenshots.takeScreenshot
     * @see uk.gov.documentchecking.utils.screenshots.screenshot
     */
    fun screenshot(suffix: String) {
        val screenshotsFolder = StringBuilder(
            "/screenshots"
        )
            .append("/${BuildConfig.BUILD_TYPE}")
            .append("/phoneController")
            .append("/${testNameRule.className}")
            .append("/${testNameRule.methodName}/")

        val parentFolder = File(
            InstrumentationRegistry.getInstrumentation().targetContext.filesDir,
            screenshotsFolder.toString()
        )

        val photoCountString = "%03d".format(photoCount)

        if (!parentFolder.exists()) {
            parentFolder.mkdirs()
        }

        val screenshotFileName = "$photoCountString - $suffix"
        val screenshotFile = File(
            parentFolder,
            "$screenshotFileName.png"
        )
        device.takeScreenshot(screenshotFile)
        try {
            BitmapFactory.decodeFile(parentFolder.absolutePath + "/$screenshotFileName.png")
                .writeToTestStorage("${screenshotsFolder}$screenshotFileName")
        } catch (exception: NullPointerException) {
            println(
                "Exception found when writing $screenshotFileName to test storage!: $exception"
            )
        }

        ++photoCount
    }

    fun elementExists(
        actionTimeoutOverride: Long = phoneActionTimeout,
        selector: BySelector
    ): Boolean {
        val element = device.wait(
            Until.findObject(selector),
            actionTimeoutOverride
        )

        return element != null && element.isEnabled
    }

    fun assertElementExists(
        actionTimeoutOverride: Long = phoneActionTimeout,
        selector: BySelector
    ) {
        val element = device.wait(
            Until.findObject(selector),
            actionTimeoutOverride
        )

        assertTrue(element != null && element.isEnabled)
    }

    fun pressKeyCode(keyCode: Int) {
        device.pressKeyCode(keyCode)
    }

    fun onRotation(action: () -> Unit) {
        onDeviceRotation(action = action)
        onDeviceRotation(action = action)

        device.unfreezeRotation()
    }

    private fun onDeviceRotation(
        onPortrait: () -> Unit = device::setOrientationLeft,
        onLandscape: () -> Unit = device::setOrientationNatural,
        action: () -> Unit
    ) {
        when (device.displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> onPortrait
            Surface.ROTATION_90, Surface.ROTATION_270 -> onLandscape
            else -> null
        }?.invoke()

        action.invoke()
    }

    fun launchDeeplink(
        context: Context,
        packageName: String,
        deeplinkUrl: String,
        timeout: Long
    ) {
        device.pressHome()
        Thread.sleep(postActionDelay)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setComponent(ComponentName(context, MainActivity::class.java))
            data = Uri.parse(deeplinkUrl)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ContextCompat.startActivity(context, intent, null)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(packageName).depth(0)),
            timeout
        )
    }

    fun navigateToApp(
        actionTimeoutOverride: Long = phoneActionTimeout,
        packageName: String
    ) {
        // Start from the home screen
        device.pressHome()

        screenshot("Press device home button")

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        ViewMatchers.assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            actionTimeoutOverride
        )

        // Launch the app
        try {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val intent =
                Intent.makeMainActivity(ComponentName(context, MainActivity::class.java))?.apply {
                    // Clear out any previous instances
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            println("Package: ${intent?.`package`}")

            context.startActivity(intent)
        } catch (exception: Exception) {
            val message = "Error found: $exception"
            throw PhoneControllerException(message, exception)
        }

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(packageName).depth(0)),
            actionTimeoutOverride
        )
    }

    /**
     * This allows the UI component children to be clickable.
     * For example, being able to access elements within a dialogue screen.
     */
    fun clickChildComponent(selectorEntry: BySelectorEntry) {
        val selector = selectorEntry.first
        val selectorMessage = selectorEntry.second

        // Create instance of child component
        val button: UiObject2 = device.findObject(selector)
        try {
            button.click()
        } catch (exception: Exception) {
            val message = "Error found with \"$selectorMessage\": $exception"
            throw PhoneControllerException(message, exception)
        }
    }
}
