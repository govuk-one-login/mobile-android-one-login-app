package uk.gov.onelogin.e2e.verify

import androidx.test.uiautomator.BySelector

object Verifier {
    fun verifyElementExists(selector: BySelector) =
        TestVerifier { controller ->
            controller.apply {
                assert(
                    elementExists(actionTimeoutOverride = 6000L, selector = selector)
                )
            }
        }
}
