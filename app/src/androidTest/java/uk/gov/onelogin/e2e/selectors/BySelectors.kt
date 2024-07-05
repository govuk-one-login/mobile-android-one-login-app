package uk.gov.onelogin.e2e.selectors

import android.content.Context
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import uk.gov.android.onelogin.R

object BySelectors {
    fun continueButton(context: Context): BySelector =
        By.text(context.getString(R.string.app_continue))

    fun loginButton(context: Context): BySelector =
        By.text(context.getString(R.string.app_signInButton))

    fun loginTitle(context: Context): BySelector =
        By.text(context.getString(R.string.app_signInTitle))

    fun loginErrorTitle(context: Context): BySelector =
        By.text(context.getString(R.string.app_signInErrorTitle))

    fun passcodeInfoTitle(context: Context): BySelector =
        By.text(context.getString(R.string.app_noPasscodePatternSetupTitle))

    fun bioOptInTitle(context: Context): BySelector =
        By.text(context.getString(R.string.app_enableBiometricsTitle))

    fun enableBiometricsButton(context: Context): BySelector =
        By.text(context.getString(R.string.app_enableBiometricsButton))

    fun homeTitle(context: Context): BySelector =
        By.text(context.getString(R.string.app_homeTitle))
}
