package uk.gov.onelogin.integrity.appcheck

import android.content.Context

interface AppChecker {
    fun init(context: Context)

    fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
