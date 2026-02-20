package uk.gov.onelogin.core.navigation.data

import kotlin.test.Test
import kotlin.test.assertEquals

class SignOutRoutesTest {
    @Test
    fun verifyRoutes() {
        listOf(
            SignOutRoutes.Root.getRoute() to "/sign_out",
            SignOutRoutes.Start.getRoute() to "/sign_out/start",
            SignOutRoutes.ReAuth.getRoute() to "/sign_out/re_auth",
            SignOutRoutes.ReAuthError.getRoute() to "/sign_out/re_auth_error",
            SignOutRoutes.Success.getRoute() to "sign_out/success",
        ).forEach { assertEquals(it.second, it.first) }
    }
}
