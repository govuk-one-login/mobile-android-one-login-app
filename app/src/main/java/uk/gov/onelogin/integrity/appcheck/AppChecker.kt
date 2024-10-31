package uk.gov.onelogin.integrity.appcheck

import android.content.Context
import uk.gov.onelogin.integrity.model.AppCheckToken

interface AppChecker {
    fun init(context: Context)

    suspend fun getAppCheckToken(): Result<AppCheckToken>
}
