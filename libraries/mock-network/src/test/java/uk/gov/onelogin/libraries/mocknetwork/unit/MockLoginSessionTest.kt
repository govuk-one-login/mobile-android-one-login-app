package uk.gov.onelogin.libraries.mocknetwork.unit

import android.content.Context
import android.content.Intent
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.onelogin.libraries.mocknetwork.auth.MockLoginSession

class MockLoginSessionTest {

    private val mockLoginSession = MockLoginSession(mock<Context>())

    @Test
    fun `finalise reports a failure and never succeeds`() {
        var success: TokenResponse? = null
        var failure: Throwable? = null

        mockLoginSession.finalise(
            intent = mock<Intent>(),
            appIntegrity = AppIntegrityParameters(attestation = "", pop = ""),
            httpServiceDomain = "https://mock.example/token",
            onSuccess = { success = it },
            onFailure = { failure = it },
        )

        assertTrue(success == null)
        assertFalse(failure == null)
        assertInstanceOf(AuthenticationError::class.java, failure)
    }
}
