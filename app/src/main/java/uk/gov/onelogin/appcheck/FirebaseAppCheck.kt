package uk.gov.onelogin.appcheck

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import kotlinx.coroutines.tasks.await
import uk.gov.onelogin.integrity.appcheck.AppChecker
import uk.gov.onelogin.integrity.model.AppCheckToken
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

    override suspend fun getAppCheckToken(): Result<AppCheckToken> {
        return try {
            Result.success(
                AppCheckToken(Firebase.appCheck.getAppCheckToken(false).await().token)
            )
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }
}
