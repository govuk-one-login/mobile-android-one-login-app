package uk.gov.onelogin.features.unit.test.login.domain.appintegrity

import android.content.Context
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.counter.CounterImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.features.featureflags.data.AppIntegrityFeatureFlag
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityImpl
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import java.security.SignatureException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppIntegrityImplTest {
    private lateinit var context: Context
    private lateinit var featureFlags: FeatureFlags
    private lateinit var appCheck: AppIntegrityManager
    private lateinit var saveToOpenSecureStore: SaveToOpenSecureStore
    private lateinit var getFromOpenSecureStore: GetFromOpenSecureStore
    private lateinit var counter: Counter

    private lateinit var sut: uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity

    @BeforeTest
    fun setup() {
        featureFlags = mock()
        appCheck = mock()
        saveToOpenSecureStore = mock()
        getFromOpenSecureStore = mock()
        context = mock()
        counter = CounterImpl()
        sut =
            AppIntegrityImpl(
                context,
                featureFlags,
                appCheck,
                saveToOpenSecureStore,
                getFromOpenSecureStore,
                counter
            )
    }

    @Test
    fun `get client attestation - feature flag disabled`() =
        runTest {
            whenever(featureFlags[eq(AppIntegrityFeatureFlag.ENABLED)]).thenReturn(false)
            whenever(getFromOpenSecureStore(CLIENT_ATTESTATION)).thenReturn(attestationSsResult)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(validAttestationExpSSResult)

            val result = sut.getClientAttestation()
            assertEquals(AttestationResult.NotRequired(ATTESTATION), result)
        }

    @Test
    fun `get client attestation - feature flag disabled and no saved attestation`() =
        runTest {
            whenever(featureFlags[eq(AppIntegrityFeatureFlag.ENABLED)]).thenReturn(false)
            whenever(getFromOpenSecureStore(eq(CLIENT_ATTESTATION))).thenReturn(null)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(validAttestationExpSSResult)

            val result = sut.getClientAttestation()
            assertEquals(AttestationResult.NotRequired(null), result)
        }

    @Test
    fun `get client attestation - attestation call successful`() =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(validAttestationExpSSResult)
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(false)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            whenever(appCheck.getExpiry(SUCCESS)).thenReturn(100L)
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION_EXPIRY, 100L)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation already stored in secure store`() =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(getFromOpenSecureStore(CLIENT_ATTESTATION)).thenReturn(attestationSsResult)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(validAttestationExpSSResult)
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            val result = sut.getClientAttestation()
            assertEquals(AttestationResult.NotRequired(ATTESTATION), result)
        }

    @Test
    fun `get client attestation - saved attestation does not match saved jwks`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(validAttestationExpSSResult)
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(false)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation stored is expired`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(invalidAttestationExpSSResult)
            println(invalidAttestationExpSSResult)
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation expiry time is null`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(mapOf(CLIENT_ATTESTATION to ATTESTATION))
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation expiry time is empty`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(
                mapOf(
                    CLIENT_ATTESTATION_EXPIRY to "",
                    CLIENT_ATTESTATION to ATTESTATION
                )
            )
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation is not stored`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(
                mapOf(
                    CLIENT_ATTESTATION_EXPIRY to VALID_ATTESTATION_EXP
                )
            )
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - saved attestation is empty`(): Unit =
        runTest {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(
                getFromOpenSecureStore.invoke(
                    CLIENT_ATTESTATION_EXPIRY,
                    CLIENT_ATTESTATION
                )
            ).thenReturn(
                mapOf(
                    CLIENT_ATTESTATION_EXPIRY to VALID_ATTESTATION_EXP,
                    CLIENT_ATTESTATION to ""
                )
            )
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            val result = sut.getClientAttestation()

            verify(saveToOpenSecureStore).save(CLIENT_ATTESTATION, SUCCESS)
            assertEquals(AttestationResult.Success(SUCCESS), result)
        }

    @Test
    fun `get client attestation - attestation call failure - GENERAL`() =
        runTest {
            val expectedError = AppIntegrityException.ClientAttestationException(Exception())
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(appCheck.getAttestation()).thenReturn(
                AttestationResponse.Failure(reason = FAILURE, error = expectedError)
            )
            val result = sut.getClientAttestation()

            assertEquals(AttestationResult.Failure(expectedError), result)
            assertEquals(0, counter.getValue())
        }

    @Test
    fun `get client attestation - attestation call failure - INTERMITTENT`() =
        runTest {
            val expectedError =
                AppIntegrityException.ClientAttestationException(
                    Exception(),
                    AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                )

            whenever(featureFlags[any()]).thenReturn(true)
            whenever(appCheck.getAttestation()).thenReturn(
                AttestationResponse.Failure(reason = FAILURE, error = expectedError)
            )
            val result = sut.getClientAttestation()

            assertEquals(AttestationResult.Failure(expectedError), result)
            assertEquals(0, counter.getValue())
        }

    @Test
    fun `get client attestation - attestation call failure - no known app integrity error`() =
        runTest {
            val expectedError = Exception()

            whenever(featureFlags[any()]).thenReturn(true)
            whenever(appCheck.getAttestation()).thenReturn(
                AttestationResponse.Failure(reason = FAILURE, error = expectedError)
            )
            val result = sut.getClientAttestation()

            assertEquals(AttestationResult.Failure(expectedError), result)
            assertEquals(0, counter.getValue())
        }

    @Test
    fun `generate Proof of Possession - success`() {
        whenever(appCheck.generatePoP(any(), any()))
            .thenReturn(SignedPoP.Success(popJwt = SUCCESS))
        whenever(context.getString(any()))
            .thenReturn("")

        val result = sut.getProofOfPossession()
        assertEquals(SignedPoP.Success(SUCCESS), result)
    }

    @Test
    fun `generate Proof of Possession - failure`() {
        val exp = SignatureException(FAILURE)
        whenever(appCheck.generatePoP(any(), any()))
            .thenReturn(SignedPoP.Failure(exp.message!!, exp))
        whenever(context.getString(any()))
            .thenReturn("")

        val result = sut.getProofOfPossession()
        assertEquals(SignedPoP.Failure(exp.message!!, exp), result)
    }

    @Test
    fun `retrieve saved ClientAttestation success`() =
        runTest {
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
                .thenReturn(attestationSsResult)
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY, CLIENT_ATTESTATION))
                .thenReturn(validAttestationExpSSResult)
            val result = sut.retrieveSavedClientAttestation()

            assertEquals(ATTESTATION, result)
        }

    @Test
    fun `retrieve saved ClientAttestation failure`() =
        runTest {
            whenever(featureFlags[AppIntegrityFeatureFlag.ENABLED]).thenReturn(true)
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
                .thenReturn(attestationSsResult)
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY, CLIENT_ATTESTATION))
                .thenReturn(invalidAttestationExpSSResult)
            val result = sut.retrieveSavedClientAttestation()

            assertEquals(null, result)
        }

    companion object {
        private const val SUCCESS = "Success"
        private const val FAILURE = "Failure"
        private const val ATTESTATION = "testAttestation"
        private val attestationSsResult = mapOf(CLIENT_ATTESTATION to ATTESTATION)
        private val VALID_ATTESTATION_EXP = "${(getTimeMillis() + getFiveMinInMillis()) / 1000}"
        private val INVALID_ATTESTATION_EXP = "${(getTimeMillis() - (getFiveMinInMillis())) / 1000}"
        private val validAttestationExpSSResult =
            mapOf(
                CLIENT_ATTESTATION_EXPIRY to VALID_ATTESTATION_EXP,
                CLIENT_ATTESTATION to ATTESTATION
            )
        private val invalidAttestationExpSSResult =
            mapOf(
                CLIENT_ATTESTATION_EXPIRY to INVALID_ATTESTATION_EXP,
                CLIENT_ATTESTATION to ATTESTATION
            )

        private fun getFiveMinInMillis(): Int = 5 * 60000
    }
}
