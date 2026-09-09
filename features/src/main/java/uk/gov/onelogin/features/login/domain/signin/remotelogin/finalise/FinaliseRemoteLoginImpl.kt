package uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import javax.inject.Inject

class FinaliseRemoteLoginImpl
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val appIntegrity: AppIntegrity,
        private val loginSession: LoginSession,
        private val logger: Logger,
    ) : FinaliseRemoteLogin {
        override suspend fun handle(intent: Intent): FinaliseRemoteLogin.Result {
            val savedAttestation = appIntegrity.retrieveSavedClientAttestation()
            // Attempt to get a new attestation if the saved one is not available due to device or open secure store
            // Very unlikely to occur
            val attestation = if (savedAttestation.isNullOrEmpty()) {
                getClientAttestation().getOrElse {
                    return FinaliseRemoteLogin.Result.Failure(it)
                }
            } else {
                savedAttestation
            }

            val popJwt = createPop(attestation = attestation).getOrElse {
                return FinaliseRemoteLogin.Result.Failure(it)
            }

            val tokens = finaliseLogin(intent, attestation, popJwt).getOrElse {
                return FinaliseRemoteLogin.Result.Failure(it)
            }

            return FinaliseRemoteLogin.Result.Success(tokens)
        }

        private suspend fun finaliseLogin(
            intent: Intent,
            attestation: String,
            jwt: String,
        ): Result<TokenResponse> {
            val tokenEndpoint =
                context.getString(
                    R.string.stsUrl,
                    context.getString(R.string.tokenExchangeEndpoint),
                )

            val result = loginSession.finaliseInternal(
                intent = intent,
                appIntegrity = AppIntegrityParameters(attestation, jwt),
                httpServiceDomain = tokenEndpoint,
            )

            result.onFailure { authError ->
                logger.error(
                    authError.javaClass.simpleName,
                    authError.message ?: NO_MESSAGE,
                    authError,
                )
            }

            return result
        }

        private suspend fun getClientAttestation(): Result<String> =
            when (val attestation = appIntegrity.getClientAttestation()) {
                is AttestationResult.Failure ->
                    Result.failure(attestation.error)

                is AttestationResult.NotRequired ->
                    Result.success(
                        attestation.savedAttestation ?: "",
                    )

                is AttestationResult.Success ->
                    Result.success(
                        attestation.clientAttestation,
                    )
            }

        @Suppress("TooGenericExceptionCaught")
        private fun createPop(
            attestation: String,
        ): Result<String> {
            if (attestation.isNotEmpty()) {
                when (val popResult = appIntegrity.getProofOfPossession()) {
                    is SignedPoP.Success ->
                        return Result.success(popResult.popJwt)

                    is SignedPoP.Failure -> {
                        val exception = popResult.error ?: IllegalStateException(popResult.reason)
                        logError(exception, popResult.reason)
                        return Result.failure(exception)
                    }
                }
            } else {
                return Result.success("")
            }
        }

        private fun logError(
            e: Throwable?,
            reason: String,
        ) {
            val error = AppIntegrityException.ProofOfPossessionException(e ?: Exception(reason))
            logger.error(
                error.javaClass.simpleName,
                error.message ?: reason,
                error,
            )
        }

        /**
         * Wrap [LoginSession.finalise] which is a callback based API
         */
        private suspend fun LoginSession.finaliseInternal(
            intent: Intent,
            appIntegrity: AppIntegrityParameters,
            httpServiceDomain: String,
        ): Result<TokenResponse> =
            CompletableDeferred<Result<TokenResponse>>().also { deferred ->
                finalise(
                    intent = intent,
                    appIntegrity = appIntegrity,
                    httpServiceDomain = httpServiceDomain,
                    onSuccess = {
                        deferred.complete(Result.success(it))
                    },
                    onFailure = {
                        deferred.complete(Result.failure(it))
                    }
                )
            }.await()

        companion object {
            private const val NO_MESSAGE = "No message"
        }
    }
