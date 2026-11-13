package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.isIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

class MockCriOrchestratorSdkTest {

    @Test
    fun `is a CriOrchestratorSdk`() {
        assertNotNull(MockCriOrchestratorSdk() as CriOrchestratorSdk)
    }

    @Test
    fun `exposes an app graph`() {
        assertNotNull(MockCriOrchestratorSdk().appGraph)
    }

    @Test
    fun `id check sdk is inactive by default`() {
        val mockCriOrchestratorSdk = MockCriOrchestratorSdk()

        assertFalse(mockCriOrchestratorSdk.isIdCheckSdkActive.invoke())
    }

    @Test
    fun `id check sdk reports active when configured active`() {
        val mockCriOrchestratorSdk = MockCriOrchestratorSdk(isIdCheckSdkActive = true)

        assertTrue(mockCriOrchestratorSdk.isIdCheckSdkActive.invoke())
    }

    @Test
    fun `id check sdk reports inactive when configured inactive`() {
        val mockCriOrchestratorSdk = MockCriOrchestratorSdk(isIdCheckSdkActive = false)

        assertFalse(mockCriOrchestratorSdk.isIdCheckSdkActive.invoke())
    }
}
