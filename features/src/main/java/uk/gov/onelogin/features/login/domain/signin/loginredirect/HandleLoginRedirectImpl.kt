package uk.gov.onelogin.features.login.domain.signin.loginredirect

import android.content.Intent
import javax.inject.Inject
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.logging.api.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult

class HandleLoginRedirectImpl @Inject constructor(
    private val appIntegrity: AppIntegrity,
    private val loginSession: LoginSession,
    private val logger: Logger
) : HandleLoginRedirect {
    override suspend fun handle(
        intent: Intent,
        onFailure: (Throwable?) -> Unit,
        onSuccess: (TokenResponse) -> Unit
    ) {
        val savedAttestation = appIntegrity.retrieveSavedClientAttestation()
        // Attempt to get a new attestation if the saved one is not available due to device or open secure store
        // Very unlikely to occur
        if (savedAttestation.isNullOrEmpty()) {
            handleGetClientAttestation(
                onSuccess = { attestation ->
                    handleCreatePoP(
                        attestation = attestation,
                        onSuccess = { jwt ->
                            handleLoginFinalise(intent, attestation, jwt, onSuccess, onFailure)
                        },
                        onFailure = onFailure
                    )
                },
                onFailure = onFailure
            )
            // Attestation retrieved successfully, directly create PoP
        } else {
            handleCreatePoP(
                attestation = savedAttestation,
                onSuccess = { jwt ->
                    handleLoginFinalise(intent, savedAttestation, jwt, onSuccess, onFailure)
                },
                onFailure = onFailure
            )
        }
    }

    private fun handleLoginFinalise(
        intent: Intent,
        attestation: String,
        jwt: String,
        onSuccess: (TokenResponse) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        loginSession.finalise(
            intent = intent,
            appIntegrity = AppIntegrityParameters(attestation, jwt),
            { tokens ->
                onSuccess(tokens)
            },
            { authError ->
                logger.error(
                    authError.javaClass.simpleName,
                    authError.message ?: NO_MESSAGE,
                    authError
                )
                onFailure(authError)
            }
        )
    }

    private suspend fun handleGetClientAttestation(
        onSuccess: (String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        when (val attestation = appIntegrity.getClientAttestation()) {
            is AttestationResult.Failure ->
                onFailure(Error(attestation.error))

            is AttestationResult.NotRequired ->
                onSuccess(
                    attestation.savedAttestation ?: ""
                )

            is AttestationResult.Success ->
                onSuccess(
                    attestation.clientAttestation
                )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleCreatePoP(
        attestation: String,
        onSuccess: (popJwt: String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        if (attestation.isNotEmpty()) {
            when (val popResult = appIntegrity.getProofOfPossession()) {
                is SignedPoP.Success ->
                    try {
                        onSuccess(popResult.popJwt)
                    } catch (e: Throwable) {
                        // handle both Error and Exception types.
                        // Includes AuthenticationError
                        onFailure(e)
                    }

                is SignedPoP.Failure -> {
                    logError(popResult.error, popResult.reason)
                    onFailure(popResult.error)
                }
            }
        } else {
            try {
                onSuccess("")
            } catch (e: Throwable) {
                // handle both Error and Exception types.
                // Includes AuthenticationError
                onFailure(e)
            }
        }
    }

    private fun logError(e: Throwable?, reason: String) {
        val error = AppIntegrity.Companion.ProofOfPossessionException(e)
        logger.error(
            error.javaClass.simpleName,
            error.message ?: reason,
            error
        )
    }

    companion object {
        private const val NO_MESSAGE = "No message"
    }
}
