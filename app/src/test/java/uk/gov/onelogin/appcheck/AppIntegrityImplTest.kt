package uk.gov.onelogin.appcheck

import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import uk.gov.android.features.FeatureFlags
import uk.gov.onelogin.integrity.ClientAttestationManager
import uk.gov.onelogin.integrity.model.AttestationResponse

class AppIntegrityImplTest {
    private val featureFlags: FeatureFlags = mock()
    private val appCheck: ClientAttestationManager = mock()

    private val sut = AppIntegrityImpl(featureFlags, appCheck)

    @Test
    fun `start check - feature flag disabled`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(false)
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.NotRequired, result)
    }

    @Test
    fun `start check - firebase token call successful`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation()).thenReturn(AttestationResponse.Success(SUCCESS))
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.Success(SUCCESS), result)
    }

    @Test
    fun `start check - firebase token call failure`() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(appCheck.getAttestation()).thenReturn(
            AttestationResponse.Failure(reason = FAILURE, error = Exception(FAILURE))
        )
        val result = sut.startCheck()
        assertEquals(AppIntegrityResult.Failure(FAILURE), result)
    }

    companion object {
        private const val SUCCESS = "Success"
        private const val FAILURE = "Failure"
    }
}
