package uk.gov.onelogin.e2e.verify

import uk.gov.onelogin.e2e.controller.PhoneController

fun interface TestVerifier {
    /**
     * Completes the verification necessary for the given [Verifier].
     *
     * The UI elements that the verifier checks against with are specific to the implementation.
     *
     * @param controller The [PhoneController] used for interacting in an automated way.
     */
    fun verify(controller: PhoneController)
}
