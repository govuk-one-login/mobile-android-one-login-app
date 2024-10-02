package uk.gov.onelogin.developer.appcheck

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize

class FirebaseAppCheck(
    private val firebase: Firebase = Firebase
) : AppCheck {
    override fun init(
        context: Context,
        appCheckFactory: AppCheckProviderFactory
    ) {
        firebase.initialize(context)
        firebase.appCheck.installAppCheckProviderFactory(
            appCheckFactory
        )
    }

    override fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebase.appCheck.getAppCheckToken(false)
            .addOnSuccessListener { token ->
                onSuccess(token.token)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
