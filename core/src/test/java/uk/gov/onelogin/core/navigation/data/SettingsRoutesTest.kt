package uk.gov.onelogin.core.navigation.data

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRoutesTest {
    @Test
    fun verifyRoutes() {
        listOf(
            SettingsRoutes.Ossl.getRoute() to "/settings/ossl",
        ).forEach { assertEquals(it.second, it.first) }
    }
}
