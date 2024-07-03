package uk.gov.onelogin.e2e

import uk.gov.onelogin.e2e.controller.PhoneController

/**
 * Functional interface that acts as an abstraction for performing a section of the User's journey.
 */
fun interface JourneyController {

    /**
     * Completes the User interaction necessary for the given [JourneyController].
     *
     * The UI elements that the User interacts with are specific to the implementation.
     *
     * @param controller The [PhoneController] used for interacting in an automated way.
     */
    fun performJourney(controller: PhoneController)
}
