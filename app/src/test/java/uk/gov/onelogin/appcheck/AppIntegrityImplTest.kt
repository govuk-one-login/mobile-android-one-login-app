package uk.gov.onelogin.appcheck

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.tokens.usecases.GetFromOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore

class AppIntegrityImplTest {
    private lateinit var featureFlags: FeatureFlags
    private lateinit var appCheck: ClientAttestationManager
    private lateinit var saveToOpenSecureStore: SaveToOpenSecureStore
    private lateinit var getFromOpenSecureStore: GetFromOpenSecureStore

    private lateinit var sut: AppIntegrity

    @BeforeTest
    fun setup() {
        featureFlags = mock()
        appCheck = mock()
        saveToOpenSecureStore = mock()
        getFromOpenSecureStore = mock()
        sut = AppIntegrityImpl(
            featureFlags,
            appCheck,
            saveToOpenSecureStore,
            getFromOpenSecureStore
        )
    }

    @Test
    fun `start check - feature flag disabled`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(false)
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.NotRequired, result)
    }

    @Test
    fun `start check - attestation call successful`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.Success(SUCCESS), result)
    }

    @Test
    fun `start check - attestation already stored in secure store`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(getFromOpenSecureStore.invoke(CLIENT_ATTESTATION)).thenReturn("Success")
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.NotRequired, result)
    }

    @Test
    fun `start check - attestation call failure`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation()).thenReturn(
            AttestationResponse.Failure(reason = FAILURE, error = Exception(FAILURE))
        )
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.Failure(FAILURE), result)
    }

    @Test
    fun `start check - save to secure store failure`() = runBlocking {
        val sse = SecureStorageError(Exception("Error"))
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation())
            .thenReturn(AttestationResponse.Success(SUCCESS, 0))
        whenever(saveToOpenSecureStore.invoke(any(), any()))
            .thenThrow(sse)
        val result = sut.startCheck()

        assertEquals(AppIntegrityResult.Failure(sse.message!!), result)
    }

    companion object {
        private const val SUCCESS = "Success"
        private const val FAILURE = "Failure"
    }
}
