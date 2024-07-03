package uk.gov.onelogin.e2e.selectors

import android.content.Context
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import uk.gov.android.onelogin.R

object BySelectors {
    fun loginButton(context: Context): BySelector =
        By.text(context.getString(R.string.app_signInButton))
}
