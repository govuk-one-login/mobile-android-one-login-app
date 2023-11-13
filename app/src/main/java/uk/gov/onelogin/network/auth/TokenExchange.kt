package uk.gov.onelogin.network.auth

import uk.gov.onelogin.network.utils.IOnlineChecker

class TokenExchange constructor(
    code: String,
    onlineChecker: IOnlineChecker
) {
    init {
        if (code.isEmpty()) {
            throw TokenExchangeCodeArgError("Code should be a non-empty string")
        }

        if (!onlineChecker.isOnline()) {
            throw TokenExchangeOfflineError("The device appears to be offline")
        }
    }

    companion object {
        class TokenExchangeCodeArgError constructor(message: String): Error(message)
        class TokenExchangeOfflineError constructor(message: String): Error(message)
    }
}
