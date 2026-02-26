package uk.gov.onelogin.login.appintegrity

import android.content.Context
import com.google.android.play.core.integrity.IntegrityServiceException
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

    // For error mappings, see: https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
    init {
        try {
            Firebase.appCheck.installAppCheckProviderFactory(
                appCheckFactory,
            )
            Firebase.initialize(context)
        } catch (integrityExp: IntegrityServiceException) {
            val exp = handleAndConvertPlayIntegrityError(integrityExp)
            throw exp
        } catch (firebaseExp: FirebaseException) {
            val exp = AppIntegrity.AppIntegrityException.FirebaseException(firebaseExp)
            logError(exp)
            throw exp
        } catch (e: Throwable) {
            val exp = AppIntegrity.AppIntegrityException.Generic(e)
            logError(exp)
            throw exp
        }
    }

    @Suppress("TooGenericExceptionCaught")
    // For error mappings, see: https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
    override suspend fun getAppCheckToken(): Result<AppCheckToken> =
        try {
            FirebaseException("", Exception())
            Result.success(
                AppCheckToken(appCheck.limitedUseAppCheckToken.await().token),
            )
        } catch (integrityExp: IntegrityServiceException) {
            val exp = handleAndConvertPlayIntegrityError(integrityExp)
            Result.failure(exp)
        } catch (firebaseExp: FirebaseException) {
            val exp = AppIntegrity.AppIntegrityException.FirebaseException(firebaseExp)
            logError(exp)
            Result.failure(exp)
        } catch (e: Throwable) {
            val exp = AppIntegrity.AppIntegrityException.FirebaseException(e)
            logError(exp)
            Result.failure(exp)
        }

    private fun logError(e: Throwable) {
        logger.error(
            e.javaClass.simpleName,
            e.message ?: NO_MESSAGE,
            e,
        )
    }

    private fun handleAndConvertPlayIntegrityError(
        e: IntegrityServiceException
    ): AppIntegrity.AppIntegrityException.FirebaseException {
        val errorType = when (e.errorCode) {
            // Retryable errors - https://developer.android.com/google/play/integrity/error-codes#iErr_3
            // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
            -3, -8, -12, -18, -100, -102
                -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT

            // Non-retryable known errors - https://developer.android.com/google/play/integrity/error-codes#iErr_3
            // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
            -1, -2, -4, -5, -6, -7, -9, -10, -11, -13, -14, -15, -16, -17, -19, -101, -103
                -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED

            // All other unknown errors
            else -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.GENERIC
        }
        val exp = AppIntegrity.AppIntegrityException.FirebaseException(e, errorType)
        logError(exp)
        return exp
    }

    companion object {
        private const val NO_MESSAGE = "No message"
    }
}
