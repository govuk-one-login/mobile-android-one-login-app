package uk.gov.onelogin.login.appintegrity

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import kotlinx.coroutines.tasks.await
import uk.gov.android.authentication.integrity.appcheck.model.AppCheckToken
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class FirebaseAppCheck
    @Inject
    constructor(
        appCheckFactory: AppCheckProviderFactory,
        context: Context,
        private val logger: Logger,
    ) : AppChecker {
        private val appCheck = Firebase.appCheck

        init {
            try {
                Firebase.appCheck.installAppCheckProviderFactory(
                    appCheckFactory,
                )
                Firebase.initialize(context)
            } catch (e: Throwable) {
                logError(e)
            }
        }

        @Suppress("TooGenericExceptionCaught")
        override suspend fun getAppCheckToken(): Result<AppCheckToken> =
            try {
                Result.success(
                    AppCheckToken(appCheck.limitedUseAppCheckToken.await().token),
                )
            } catch (e: FirebaseException) {
                logError(e)
                Result.failure(e)
            } catch (e: Throwable) {
                logError(e)
                Result.failure(e)
            }

        private fun logError(e: Throwable) {
            val error = AppIntegrity.Companion.FirebaseException(e)
            logger.error(
                error.javaClass.simpleName,
                error.message ?: NO_MESSAGE,
                error,
            )
        }

        companion object {
            private const val NO_MESSAGE = "No message"
        }
    }
