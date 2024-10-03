package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.onelogin.core.delete.domain.Cleaner
import uk.gov.onelogin.tokens.Keys.TOKEN_EXPIRY_KEY
import uk.gov.onelogin.tokens.Keys.TOKEN_SHARED_PREFS

interface RemoveTokenExpiry : Cleaner

class RemoveTokenExpiryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : RemoveTokenExpiry {
    private val sharedPrefs =
        context.getSharedPreferences(TOKEN_SHARED_PREFS, Context.MODE_PRIVATE)

    override suspend fun clean(): Result<Unit> {
        with(sharedPrefs.edit()) {
            remove(TOKEN_EXPIRY_KEY)
            commit()
        }
        return Result.success(Unit)
    }
}
