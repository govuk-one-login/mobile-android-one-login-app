package uk.gov.onelogin.core.navigation.data

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeRoutesTest {
    @Test
    fun verifyRoutes() {
        assertEquals("/home/how_to_prove_your_identity", HomeRoutes.HowToProveYourIdentity.getRoute())
    }
}
