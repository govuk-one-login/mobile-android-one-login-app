package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.onelogin.tokens.Keys.TOKEN_EXPIRY_KEY
import uk.gov.onelogin.tokens.Keys.TOKEN_SHARED_PREFS

fun interface RemoveTokenExpiry {
    /**
     * Use case to remove the expiry time of the token in open shared preferences
     */
    operator fun invoke()
}

class RemoveTokenExpiryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : RemoveTokenExpiry {
    private val sharedPrefs =
        context.getSharedPreferences(TOKEN_SHARED_PREFS, Context.MODE_PRIVATE)

    override fun invoke() {
        with(sharedPrefs.edit()) {
            remove(TOKEN_EXPIRY_KEY)
            apply()
        }
    }
}
