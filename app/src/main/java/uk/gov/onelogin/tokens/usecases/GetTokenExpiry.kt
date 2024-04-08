package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.onelogin.tokens.Keys.TOKEN_EXPIRY_KEY
import uk.gov.onelogin.tokens.Keys.TOKEN_SHARED_PREFS

fun interface GetTokenExpiry {
    /**
     * Use case to get the expiry time of the token in open shared preferences
     *
     * @return expiry Long type timestamp of expiry time
     */
    operator fun invoke(): Long?
}

class GetTokenExpiryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : GetTokenExpiry {
    private val sharedPrefs = context.getSharedPreferences(TOKEN_SHARED_PREFS, Context.MODE_PRIVATE)
    override fun invoke(): Long? {
        val expiryTimestamp = sharedPrefs.getLong(TOKEN_EXPIRY_KEY, 0)
        return if (expiryTimestamp == 0L) {
            null
        } else {
            expiryTimestamp
        }
    }
}
