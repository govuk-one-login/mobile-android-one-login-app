package uk.gov.onelogin.e2e.controller

class PhoneControllerException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)
