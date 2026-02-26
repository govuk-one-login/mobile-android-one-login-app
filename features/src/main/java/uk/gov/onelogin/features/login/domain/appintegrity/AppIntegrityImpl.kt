package uk.gov.onelogin.features.login.domain.appintegrity

import android.content.Context
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.utils.Counter
import uk.gov.onelogin.features.featureflags.data.AppIntegrityFeatureFlag
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import javax.inject.Inject

class AppIntegrityImpl
    @Inject
    constructor(
        private val context: Context,
        private val featureFlags: FeatureFlags,
        private val appCheck: AppIntegrityManager,
        private val saveToOpenSecureStore: SaveToOpenSecureStore,
        private val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val retryCounter: Counter
    ) : AppIntegrity {
        override suspend fun getClientAttestation(): AttestationResult =
            if (isAttestationCallRequired()) {
                when (val result = appCheck.getAttestation()) {
                    is AttestationResponse.Success -> handleClientAttestation(result)
                    is AttestationResponse.Failure -> handleAppIntegrityErrorFromAttestation(result = result)
                }
            } else {
                AttestationResult.NotRequired(retrieveSavedClientAttestation())
            }

        override fun getProofOfPossession(): SignedPoP =
            appCheck.generatePoP(
                iss = context.getString(R.string.stsClientId),
                aud = context.getString(R.string.baseStsUrl),
            )

        override suspend fun retrieveSavedClientAttestation(): String? {
            // Check if attestation is expired:
            return if (isAttestationCallRequired()) {
                null
            } else {
                // No need for a null check here as it's done in isAttestationCallRequired
                getFromOpenSecureStore.invoke(CLIENT_ATTESTATION)?.get(CLIENT_ATTESTATION)
            }
        }

        private suspend fun handleClientAttestation(result: AttestationResponse.Success): AttestationResult {
            // This never throws error
            saveToOpenSecureStore.save(CLIENT_ATTESTATION, result.attestationJwt)
            // This can only return null, no error to be returned
            appCheck.getExpiry(result.attestationJwt)?.let {
                // This can never throw error
                saveToOpenSecureStore.save(CLIENT_ATTESTATION_EXPIRY, it)
            }
            return AttestationResult.Success(result.attestationJwt)
        }

        private suspend fun isAttestationCallRequired(): Boolean {
            val ssResult: Map<String, String?>? =
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION,
                )
            val exp = ssResult?.get(CLIENT_ATTESTATION_EXPIRY)
            val attestation = ssResult?.get(CLIENT_ATTESTATION)

            val result =
                isAttestationExpired(exp) ||
                    attestation.isNullOrEmpty() ||
                    !appCheck.verifyAttestationJwk(attestation)

            return featureFlags[AppIntegrityFeatureFlag.ENABLED] && result
        }

        private fun isAttestationExpired(expiryTime: String?): Boolean =
            if (!expiryTime.isNullOrEmpty()) {
                expiryTime.toLong() <= getTimeMillis() / CONVERT_TO_SECONDS
            } else {
                true
            }

        private suspend fun handleAppIntegrityErrorFromAttestation(
            result: AttestationResponse.Failure
        ): AttestationResult =
            when (val error = result.error) {
                // When an AppIntegrityException gets thrown from either backend or Firebase.getLimitedToken
                is AppIntegrity.AppIntegrityException -> {
                    when (error.type) {
                        // When an INTERMITTENT error is returned, attempt to get the attestation again up to 3 times
                        // before returning a result
                        AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT -> {
                            val count = retryCounter.getCount()
                            if (count < MAX_RETRY) {
                                calculateDelay(count)
                                retryCounter.incrementCount()
                                getClientAttestation()
                                // If retired 2 times already, return the error to the consumer directly
                            } else {
                                retryCounter.reset()
                                AttestationResult.Failure(error)
                            }
                        }
                        // Any other type of AppIntegrityError, return the error to the consumer directly
                        else -> {
                            retryCounter.reset()
                            AttestationResult.Failure(error)
                        }
                    }
                }
                // Any other errors, return the error to the consumer directly
                else -> {
                    retryCounter.reset()
                    AttestationResult.Failure(result.error)
                }
            }

        private suspend fun calculateDelay(count: Int) {
            if (count == FIRST_COUNT) {
                delay(ONE_SEC_DELAY)
            } else {
                delay(TWO_SEC_DELAY)
            }
        }

        companion object {
            private const val CONVERT_TO_SECONDS = 1000
            private const val MAX_RETRY = 3
            private const val FIRST_COUNT = 1
            private const val ONE_SEC_DELAY = 1000L
            private const val TWO_SEC_DELAY = 2000L
        }
    }
