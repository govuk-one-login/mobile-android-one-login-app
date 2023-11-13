package uk.gov.onelogin.network.auth

class TokenExchange constructor(
    private val code: String
) {
    init {
        if (code.isEmpty()) {
            throw TokenExchangeCodeArgError("Code should be a non-empty string")
        }
    }

    companion object {
        class TokenExchangeCodeArgError constructor(message: String): Error(message)
    }
}
