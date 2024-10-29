package uk.gov.onelogin.appcheck

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import kotlinx.coroutines.tasks.await
import uk.gov.onelogin.integrity.appcheck.AppChecker
import javax.inject.Inject

class FirebaseAppCheck @Inject constructor(
    private val appCheckFactory: AppCheckProviderFactory
) : AppChecker {
    override fun init(
        context: Context,
    ) {
        Firebase.initialize(context)
        Firebase.appCheck.installAppCheckProviderFactory(
            appCheckFactory
        )
    }

    override suspend fun getAppCheckToken(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            Firebase.appCheck.getAppCheckToken(false)
                .addOnSuccessListener { token ->
                    onSuccess(token.token)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
                .await()
        } catch (e: FirebaseException) {
            throw Exception("debug_token_null")
        }
    }
}
