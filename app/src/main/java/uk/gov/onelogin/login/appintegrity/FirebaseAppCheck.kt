package uk.gov.onelogin.login.appintegrity

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import uk.gov.android.authentication.integrity.appcheck.model.AppCheckToken
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger

class FirebaseAppCheck @Inject constructor(
    appCheckFactory: AppCheckProviderFactory,
    context: Context,
    private val logger: Logger
) : AppChecker {
    private val appCheck = Firebase.appCheck

    init {
        Firebase.appCheck.installAppCheckProviderFactory(
            appCheckFactory
        )
        Firebase.initialize(context)
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getAppCheckToken(): Result<AppCheckToken> {
        return try {
            Result.success(
                AppCheckToken(appCheck.limitedUseAppCheckToken.await().token)
            )
        } catch (e: FirebaseException) {
            logger.error(
                e.javaClass.simpleName,
                e.message ?: NO_MESSAGE,
                e
            )
            Result.failure(e)
        } catch (e: Throwable) {
            logger.error(
                e.javaClass.simpleName,
                e.message ?: NO_MESSAGE,
                e
            )
            Result.failure(e)
        }
    }

    companion object {
        private const val NO_MESSAGE = "No message"
    }
}
