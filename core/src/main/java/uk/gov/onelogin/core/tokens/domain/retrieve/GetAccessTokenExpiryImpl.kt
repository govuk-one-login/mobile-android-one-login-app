package uk.gov.onelogin.core.tokens.domain.retrieve

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.TOKEN_SHARED_PREFS
import javax.inject.Inject
import kotlin.text.toLong

class GetAccessTokenExpiryImpl
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val getFromOpenSecureStore: GetFromOpenSecureStore
    ) : GetTokenExpiry {
        override suspend fun invoke(): Long? {
            try {
                val expiryTimestamp =
                    getFromOpenSecureStore(ACCESS_TOKEN_EXPIRY_KEY)
                        ?.get(ACCESS_TOKEN_EXPIRY_KEY)
                        ?.toLong()
                return if (expiryTimestamp == 0L || expiryTimestamp == null) {
                    checkAndReturnSharedPrefsExpiry()
                } else {
                    expiryTimestamp
                }
            } catch (_: NumberFormatException) {
                return checkAndReturnSharedPrefsExpiry()
            }
        }

        private fun checkAndReturnSharedPrefsExpiry(): Long? {
            // The SharedPrefs will be removed in a period to be determined to allow for migration for all users and avoid
            // creating a breaking change that forces the user to be treated as a new user.
            val sharedPrefs =
                context.getSharedPreferences(
                    TOKEN_SHARED_PREFS,
                    Context.MODE_PRIVATE
                )
            // The SharedPrefs will be removed in a period to be determined to allow for migration for all users and avoid
            // creating a breaking change that forces the user to be treated as a new user.
            val expiryTimestampSharedPrefs = sharedPrefs.getLong(ACCESS_TOKEN_EXPIRY_KEY, 0)
            return if (expiryTimestampSharedPrefs == 0L) {
                null
            } else {
                expiryTimestampSharedPrefs
            }
        }
    }
