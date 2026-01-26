package uk.gov.onelogin.core.tokens.domain.retrieve

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.text.toLong
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.TOKEN_SHARED_PREFS

class GetAccessTokenExpiryImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : GetTokenExpiry {
    // The SharedPrefs will be removed in a period to be determined to allow for migration for all users and avoid
    // creating a breaking change that forces the user to be treated as a new user.
    private val sharedPrefs = context.getSharedPreferences(
        TOKEN_SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    override suspend fun invoke(): Long? {
        // The SharedPrefs will be removed in a period to be determined to allow for migration for all users and avoid
        // creating a breaking change that forces the user to be treated as a new user.
        val expiryTimestampSharedPrefs = sharedPrefs.getLong(ACCESS_TOKEN_EXPIRY_KEY, 0)
        try {
            val expiryTimestamp = getFromOpenSecureStore(ACCESS_TOKEN_EXPIRY_KEY)
                ?.get(ACCESS_TOKEN_EXPIRY_KEY)?.toLong()
            return if (expiryTimestamp == 0L || expiryTimestamp == null) {
                checkAndReturnSharedPrefsExpiry(expiryTimestampSharedPrefs)
            } else {
                expiryTimestamp
            }
        } catch (_: NumberFormatException) {
            return checkAndReturnSharedPrefsExpiry(expiryTimestampSharedPrefs)
        }
    }

    private fun checkAndReturnSharedPrefsExpiry(exp: Long): Long? {
        return if (exp == 0L) {
            null
        } else {
            exp
        }
    }
}
