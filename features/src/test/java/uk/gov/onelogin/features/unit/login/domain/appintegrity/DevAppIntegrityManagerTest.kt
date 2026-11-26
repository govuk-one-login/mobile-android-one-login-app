package uk.gov.onelogin.features.unit.login.domain.appintegrity

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityDevOptionsRepositoryImpl
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.DevAppIntegrityManager
import uk.gov.onelogin.features.login.domain.appintegrity.TestAppIntegrityManager

class DevAppIntegrityManagerTest {
    private val devOptionsRepository = AppIntegrityDevOptionsRepositoryImpl()
    private val realManager = TestAppIntegrityManager()
    private val devAppIntegrityManager = DevAppIntegrityManager(devOptionsRepository, realManager)

    @Test
    fun `getAttestation returns forced failure when forceError is set`() = runTest {
        devOptionsRepository.forceError =
            AppIntegrityException.AppIntegrityErrorType.INTERMITTENT

        val result = devAppIntegrityManager.getAttestation()

        assertInstanceOf(AttestationResponse.Failure::class.java, result)
        val error = (result as AttestationResponse.Failure).error
        assertInstanceOf(AppIntegrityException::class.java, error)
        assertEquals(
            AppIntegrityException.AppIntegrityErrorType.INTERMITTENT,
            (error as AppIntegrityException).type,
        )
    }

    @Test
    fun `getAttestation delegates to real manager when forceError is null`() = runTest {
        devOptionsRepository.forceError = null
        val expected = AttestationResponse.Success("real-attestation", 0)
        realManager.attestationResponse = expected

        val result = devAppIntegrityManager.getAttestation()

        assertEquals(expected, result)
    }

    @Test
    fun `generatePoP delegates to real manager`() {
        val expected = SignedPoP.Success("real-pop")
        realManager.popResult = expected

        val result = devAppIntegrityManager.generatePoP("iss", "aud")

        assertEquals(expected, result)
    }

    @Test
    fun `verifyAttestationJwk delegates to real manager`() {
        realManager.verifyResult = true

        val result = devAppIntegrityManager.verifyAttestationJwk("attestation")

        assertEquals(true, result)
    }

    @Test
    fun `getExpiry delegates to real manager`() {
        realManager.expiry = 100L

        val result = devAppIntegrityManager.getExpiry("attestation")

        assertEquals(100L, result)
    }
}
