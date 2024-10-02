package uk.gov.onelogin.developer.appcheck

import android.content.Context
import com.google.firebase.appcheck.AppCheckProviderFactory

interface AppCheck {
    fun init(
        context: Context,
        appCheckFactory: AppCheckProviderFactory
    )

    fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
