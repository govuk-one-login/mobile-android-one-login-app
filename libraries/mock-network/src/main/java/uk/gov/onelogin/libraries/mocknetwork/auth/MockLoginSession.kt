package uk.gov.onelogin.libraries.mocknetwork.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.android.authentication.login.TokenResponse

/**
 * Mock implementation of [LoginSession] that replaces the real AppAuth / OAuth flow in tests.
 *
 * [present] launches [MockLoginActivity], which returns immediately so the app's real
 * activity-result handling drives it on to [finalise], keeping as much of the real login code
 * path as possible.
 *
 * For now [finalise] always reports a failure (a simple failure auth journey). Success journeys
 * will be added later, once the mocked network layer can serve the responses that the real
 * post-login token handling (e.g. JWKS verification) depends on.
 */
class MockLoginSession(private val context: Context) : LoginSession {

    override fun present(
        launcher: ActivityResultLauncher<Intent>,
        configuration: LoginSessionConfiguration,
    ) {
        launcher.launch(Intent(context, MockLoginActivity::class.java))
    }

    override fun finalise(
        intent: Intent,
        appIntegrity: AppIntegrityParameters,
        httpServiceDomain: String,
        onSuccess: (tokens: TokenResponse) -> Unit,
        onFailure: (error: Throwable) -> Unit,
    ) {
        onFailure(
            AuthenticationError(
                message = FAILURE_MESSAGE,
                type = AuthenticationError.ErrorType.SERVER_ERROR,
            ),
        )
    }

    private companion object {
        const val FAILURE_MESSAGE = "Mock login session failure"
    }
}
