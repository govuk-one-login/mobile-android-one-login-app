package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.onelogin.core.delete.domain.Cleaner
import javax.inject.Inject
import uk.gov.onelogin.tokens.Keys.TOKEN_EXPIRY_KEY
import uk.gov.onelogin.tokens.Keys.TOKEN_SHARED_PREFS

interface RemoveTokenExpiry: Cleaner {
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

    override suspend fun clean(): Result<Unit> {
        with(sharedPrefs.edit()) {
            remove(TOKEN_EXPIRY_KEY)
            commit()
        }
        return Result.success(Unit)
    }
}
