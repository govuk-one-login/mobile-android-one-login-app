package uk.gov.onelogin.core.utils

import uk.gov.android.authentication.login.TokenResponse
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens

/**
 * Converts a Token Response to LoginTokens to allow reducing the risk of saving a `refreshToken` in memory.
 */
fun TokenResponse.convertToLoginTokens(): LoginTokens =
    LoginTokens(
        tokenType = this.tokenType,
        accessToken = this.accessToken,
        accessTokenExpirationTime = this.accessTokenExpirationTime,
        idToken = this.idToken,
    )
