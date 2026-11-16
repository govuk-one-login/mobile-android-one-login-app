package uk.gov.onelogin.libraries.mocknetwork.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * A no-op activity used by [MockLoginSession] in place of the real Chrome Custom Tab login flow.
 *
 * It finishes immediately with [Activity.RESULT_OK] and a non-null (empty) data intent, so that
 * the app's real activity-result handling proceeds to call `finalise(...)` exactly as it would
 * after a genuine login. The returned intent's contents are not used by [MockLoginSession].
 */
class MockLoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_OK, Intent())
        finish()
    }
}
