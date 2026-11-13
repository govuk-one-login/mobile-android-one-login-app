package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

class MockCriOrchestratorSdkTest {
    private val mockCriOrchestratorSdk: MockCriOrchestratorSdk = MockCriOrchestratorSdk()

    @Test
    fun `is a CriOrchestratorSdk`() {
        assertNotNull(mockCriOrchestratorSdk as CriOrchestratorSdk)
    }

    @Test
    fun `exposes an app graph`() {
        assertNotNull(mockCriOrchestratorSdk.appGraph)
    }
}
