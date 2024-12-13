package uk.gov.onelogin.appcheck

import android.content.Context
import io.jsonwebtoken.security.SignatureException
import io.ktor.util.date.getTimeMillis
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.features.FeatureFlags
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import uk.gov.onelogin.features.AppCheckFeatureFlag
import uk.gov.onelogin.tokens.usecases.GetFromOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore

class AppIntegrityImplTest {
    private lateinit var context: Context
    private lateinit var featureFlags: FeatureFlags
    private lateinit var appCheck: AppIntegrityManager
    private lateinit var saveToOpenSecureStore: SaveToOpenSecureStore
    private lateinit var getFromOpenSecureStore: GetFromOpenSecureStore

    private lateinit var sut: AppIntegrity

    @BeforeTest
    fun setup() {
        featureFlags = mock()
        appCheck = mock()
        saveToOpenSecureStore = mock()
        getFromOpenSecureStore = mock()
        context = mock()
        sut = AppIntegrityImpl(
            context,
            featureFlags,
            appCheck,
            saveToOpenSecureStore,
            getFromOpenSecureStore
        )
    }

    @Test
    fun `get client attestation - feature flag disabled`() = runBlocking {
        whenever(featureFlags[eq(AppCheckFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(getFromOpenSecureStore(CLIENT_ATTESTATION)).thenReturn(attestationSsResult)

        val result = sut.getClientAttestation()
        assertEquals(AttestationResult.NotRequired(ATTESTATION), result)
    }

    @Test
    fun `get client attestation - feature flag disabled and no saved attestation`() = runBlocking {
        whenever(featureFlags[eq(AppCheckFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(getFromOpenSecureStore(eq(CLIENT_ATTESTATION))).thenReturn(null)

        val result = sut.getClientAttestation()
        assertEquals(AttestationResult.NotRequired(null), result)
    }

    @Test
    fun `get client attestation - attestation call successful`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(mapOf())
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
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
    fun `get client attestation - attestation already stored in secure store`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(validAttestationExpSSResult)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        val result = sut.getClientAttestation()
        assertEquals(AttestationResult.NotRequired(ATTESTATION), result)
    }

    @Test
    fun `get client attestation - saved attestation does not match saved jwks`(): Unit =
        runBlocking {
            whenever(featureFlags[any()]).thenReturn(true)
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
                .thenReturn(validAttestationExpSSResult)
            whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
                .thenReturn(attestationSsResult)
            whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(false)
            whenever(appCheck.getAttestation())
                .thenReturn(AttestationResponse.Success(SUCCESS, 0))
            sut.getClientAttestation()
            verify(appCheck).getAttestation()
        }

    @Test
    fun `get client attestation - attestation stored is expired`(): Unit = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(invalidAttestationExpSSResult)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        sut.getClientAttestation()
        verify(appCheck).getAttestation()
    }

    @Test
    fun `get client attestation - attestation expiry time is null`(): Unit = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(null)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        sut.getClientAttestation()
        verify(appCheck).getAttestation()
    }

    @Test
    fun `get client attestation - attestation expiry time is empty`(): Unit = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(mapOf(CLIENT_ATTESTATION_EXPIRY to ""))
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        sut.getClientAttestation()
        verify(appCheck).getAttestation()
    }

    @Test
    fun `get client attestation - attestation is not stored`(): Unit = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(validAttestationExpSSResult)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(null)
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        sut.getClientAttestation()
        verify(appCheck).getAttestation()
    }

    @Test
    fun `get client attestation - saved attestation is empty`(): Unit = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION_EXPIRY))
            .thenReturn(validAttestationExpSSResult)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(mapOf(CLIENT_ATTESTATION to ""))
        whenever(appCheck.verifyAttestationJwk(ATTESTATION)).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        sut.getClientAttestation()
        verify(appCheck).getAttestation()
    }

    @Test
    fun `get client attestation - attestation call failure`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation()).thenReturn(
            AttestationResponse.Failure(reason = FAILURE, error = Exception(FAILURE))
        )
        val result = sut.getClientAttestation()
        assertEquals(AttestationResult.Failure(FAILURE), result)
    }

    @Test
    fun `get client attestation - save to secure store failure`() = runBlocking {
        val sse = SecureStorageError(Exception(FAILURE))
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        whenever(saveToOpenSecureStore.save(any(), any<String>()))
            .thenThrow(sse)
        val result = sut.getClientAttestation()

        assertEquals(AttestationResult.Failure(sse.message!!), result)
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
    fun `retrieve saved ClientAttestation`() = runBlocking {
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION))
            .thenReturn(attestationSsResult)
        val result = sut.retrieveSavedClientAttestation()

        assertEquals(ATTESTATION, result)
    }

    companion object {
        private const val SUCCESS = "Success"
        private const val FAILURE = "Failure"
        private const val ATTESTATION = "testAttestation"
        private val attestationSsResult = mapOf(CLIENT_ATTESTATION to ATTESTATION)
        private val VALID_ATTESTATION_EXP = "${getTimeMillis() + (getFiveMinInMillis())}"
        private val validAttestationExpSSResult = mapOf(
            CLIENT_ATTESTATION_EXPIRY to VALID_ATTESTATION_EXP
        )
        private val INVALID_ATTESTATION_EXP = "${getTimeMillis() - (getFiveMinInMillis())}"
        private val invalidAttestationExpSSResult = mapOf(
            CLIENT_ATTESTATION_EXPIRY to INVALID_ATTESTATION_EXP
        )

        private fun getFiveMinInMillis(): Int {
            return 5 * 60000
        }
    }
}
