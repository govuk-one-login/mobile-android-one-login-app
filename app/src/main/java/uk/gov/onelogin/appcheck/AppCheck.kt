package uk.gov.onelogin.appcheck

import android.content.Context

interface AppCheck {
    fun init(
        context: Context
    )

    fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
