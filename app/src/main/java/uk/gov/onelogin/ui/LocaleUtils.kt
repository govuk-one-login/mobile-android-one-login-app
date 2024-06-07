package uk.gov.onelogin.ui

import android.content.Context
import androidx.core.os.ConfigurationCompat
import uk.gov.android.authentication.LoginSessionConfiguration

object LocaleUtils {
    fun getLocaleAsSessionConfig(context: Context): LoginSessionConfiguration.Locale =
        when (getLocale(context)) {
            "cy" -> LoginSessionConfiguration.Locale.CY
            else -> LoginSessionConfiguration.Locale.EN
        }
    fun getLocale(context: Context): String {
        val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        val locale = when (currentLocale?.language) {
            "cy" -> "cy"
            else -> "en"
        }
        return locale
    }
}
