package uk.gov.onelogin.appcheck

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize

class FirebaseAppCheck : AppCheck {
    override fun init(
        context: Context,
        appCheckFactory: AppCheckProviderFactory
    ) {
        Firebase.initialize(context)
        Firebase.appCheck.installAppCheckProviderFactory(
            appCheckFactory
        )
    }

    override fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Firebase.appCheck.getAppCheckToken(false)
            .addOnSuccessListener { token ->
                onSuccess(token.token)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
