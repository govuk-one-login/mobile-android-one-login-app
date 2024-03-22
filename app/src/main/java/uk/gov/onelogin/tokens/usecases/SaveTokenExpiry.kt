package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.onelogin.tokens.Keys.TOKEN_EXPIRY_KEY
import uk.gov.onelogin.tokens.Keys.TOKEN_SHARED_PREFS

interface SaveTokenExpiry {
    /**
     * Use case to save the expiry time of the token in open shared preferences
     *
     * @param expiry Long type timestamp of expiry time
     */
    operator fun invoke(expiry: Long)
}

class SaveTokenExpiryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : SaveTokenExpiry {
    private val sharedPrefs = context.getSharedPreferences(TOKEN_SHARED_PREFS, Context.MODE_PRIVATE)
    override fun invoke(expiry: Long) {
        with(sharedPrefs.edit()) {
            putLong(TOKEN_EXPIRY_KEY, expiry)
            apply()
        }
    }
}
