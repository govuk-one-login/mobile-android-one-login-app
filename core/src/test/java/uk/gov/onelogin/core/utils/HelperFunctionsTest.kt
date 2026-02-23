package uk.gov.onelogin.core.utils

import org.junit.Test
import uk.gov.android.authentication.login.TokenResponse
import kotlin.test.assertEquals

class HelperFunctionsTest {
    @Test
    fun `test TokenResponse convertToLoginTokens extension function`() {
        val tokenResponse =
            TokenResponse(
                tokenType = "token",
                accessToken = "accessToken",
                idToken = "idToken",
                accessTokenExpirationTime = 1,
                refreshToken = "refreshToken"
            )

        val actual = tokenResponse.convertToLoginTokens()

        assertEquals("token", actual.tokenType)
        assertEquals("accessToken", actual.accessToken)
        assertEquals("idToken", actual.idToken)
        assertEquals(1, actual.accessTokenExpirationTime)
        assertEquals(
            "LoginTokens(tokenType=token, accessToken=accessToken, accessTokenExpirationTime=1, idToken=idToken)",
            actual.toString()
        )
    }
}
