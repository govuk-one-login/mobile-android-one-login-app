package uk.gov.onelogin.login.appintegrity

import com.google.android.play.core.integrity.IntegrityServiceException
import com.google.firebase.FirebaseException
import uk.gov.android.authentication.integrity.appcheck.model.AppCheckToken
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import javax.inject.Inject
import kotlin.jvm.Throws

@Suppress("TooGenericExceptionCaught")
class FirebaseAppCheck
    @Inject
    constructor(
        private val provider: FirebaseAppCheckProvider,
        private val logger: Logger,
    ) : AppChecker {
        // For error mappings, see: https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
        init {
            try {
                provider.init()
                // Cannot be tested because initialising an IntegrityServiceException is private, values can only be accessed
            } catch (integrityExp: IntegrityServiceException) {
                handleAndConvertPlayIntegrityError(integrityExp)
            } catch (firebaseExp: FirebaseException) {
                val exp = AppIntegrity.AppIntegrityException.FirebaseException(firebaseExp)
                logError(exp)
            } catch (e: Throwable) {
                val exp = AppIntegrity.AppIntegrityException.Generic(e)
                logError(exp)
            }
        }

        @Throws(AppIntegrity.AppIntegrityException::class)
        @Suppress("TooGenericExceptionCaught")
        // For error mappings, see: https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
        override suspend fun getAppCheckToken(): Result<AppCheckToken> =
            try {
                Result.success(
                    AppCheckToken(provider.getToken()),
                )
                // Cannot be tested because initialising a com.google.firebase.FirebaseException is private, values can only be accessed
            } catch (integrityExp: IntegrityServiceException) {
                val exp = handleAndConvertPlayIntegrityError(integrityExp)
                Result.failure(exp)
            } catch (firebaseExp: FirebaseException) {
                val exp = AppIntegrity.AppIntegrityException.FirebaseException(firebaseExp)
                logError(exp)
                Result.failure(exp)
            } catch (e: Throwable) {
                val exp = AppIntegrity.AppIntegrityException.Generic(e)
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
            val errorType =
                when (e.errorCode) {
                    // Retryable errors - https://developer.android.com/google/play/integrity/error-codes#iErr_3
                    // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
                    INTEGRITY_NETWORK_ERROR,
                    INTEGRITY_CLIENT_TRANSIENT_ERROR,
                    INTEGRITY_INTERNAL_ERROR,
                    INTEGRITY_STANDARD_INTEGRITY_INTERNAL_ERROR,
                    INTEGRITY_GOOGLE_SERVER_UNAVAILABLE,
                    INTEGRITY_STANDARD_INTEGRITY_INITIALIZATION_FAILED,
                    INTEGRITY_TOO_MANY_REQUESTS
                    -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT

                    // Non-retryable known errors - https://developer.android.com/google/play/integrity/error-codes#iErr_3
                    // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
                    INTEGRITY_API_NOT_AVAILABLE,
                    INTEGRITY_PLAY_SERVICES_NOT_FOUND,
                    INTEGRITY_PLAY_STORE_VERSION_OUTDATED,
                    INTEGRITY_PLAY_STORE_NOT_FOUND,
                    INTEGRITY_PLAY_STORE_ACCOUNT_NOT_FOUND,
                    INTEGRITY_APP_NOT_INSTALLED,
                    INTEGRITY_APP_UID_MISMATCH,
                    INTEGRITY_CANNOT_BIND_TO_SERVICE,
                    INTEGRITY_NONCE_TOO_SHORT,
                    INTEGRITY_NONCE_TOO_LONG,
                    INTEGRITY_NONCE_IS_NOT_BASE64,
                    INTEGRITY_PLAY_SERVICES_VERSION_OUTDATED,
                    INTEGRITY_CLOUD_PROJECT_NUMBER_IS_INVALID,
                    INTEGRITY_REQUEST_HASH_TOO_LONG,
                    INTEGRITY_INTEGRITY_TOKEN_PROVIDER_INVALID,
                    INTEGRITY_STANDARD_INTEGRITY_INITIALIZATION_NEEDED,
                    INTEGRITY_STANDARD_INTEGRITY_INVALID_ARGUMENT
                    -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED

                    // All other unknown errors
                    else -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.GENERIC
                }
            val exp = AppIntegrity.AppIntegrityException.FirebaseException(e, errorType)
            logError(exp)
            return exp
        }

        companion object {
            // Retryable
            private const val INTEGRITY_NETWORK_ERROR = -3
            private const val INTEGRITY_TOO_MANY_REQUESTS = -8
            private const val INTEGRITY_GOOGLE_SERVER_UNAVAILABLE = -12
            private const val INTEGRITY_CLIENT_TRANSIENT_ERROR = -18
            private const val INTEGRITY_INTERNAL_ERROR = -100
            private const val INTEGRITY_STANDARD_INTEGRITY_INTERNAL_ERROR = INTEGRITY_INTERNAL_ERROR
            private const val INTEGRITY_STANDARD_INTEGRITY_INITIALIZATION_FAILED = -102

            // Non-retryable
            private const val INTEGRITY_API_NOT_AVAILABLE = -1
            private const val INTEGRITY_PLAY_STORE_NOT_FOUND = -2
            private const val INTEGRITY_PLAY_STORE_ACCOUNT_NOT_FOUND = -4
            private const val INTEGRITY_APP_NOT_INSTALLED = -5
            private const val INTEGRITY_PLAY_SERVICES_NOT_FOUND = -6
            private const val INTEGRITY_APP_UID_MISMATCH = -7
            private const val INTEGRITY_CANNOT_BIND_TO_SERVICE = -9
            private const val INTEGRITY_NONCE_TOO_SHORT = -10
            private const val INTEGRITY_NONCE_TOO_LONG = -11
            private const val INTEGRITY_NONCE_IS_NOT_BASE64 = -13
            private const val INTEGRITY_PLAY_STORE_VERSION_OUTDATED = -14
            private const val INTEGRITY_PLAY_SERVICES_VERSION_OUTDATED = -15
            private const val INTEGRITY_CLOUD_PROJECT_NUMBER_IS_INVALID = -16
            private const val INTEGRITY_REQUEST_HASH_TOO_LONG = -17
            private const val INTEGRITY_INTEGRITY_TOKEN_PROVIDER_INVALID = -19
            private const val INTEGRITY_STANDARD_INTEGRITY_INITIALIZATION_NEEDED = -101
            private const val INTEGRITY_STANDARD_INTEGRITY_INVALID_ARGUMENT = -103

            private const val NO_MESSAGE = "No message"
        }
    }
