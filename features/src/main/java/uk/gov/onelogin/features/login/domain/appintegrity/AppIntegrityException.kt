package uk.gov.onelogin.features.login.domain.appintegrity

sealed class AppIntegrityException(
    open val e: Throwable,
    open val type: AppIntegrityErrorType
) : Exception(e) {
    data class FirebaseException(
        override val e: Throwable,
        override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
    ) : AppIntegrityException(e, type)

    data class ClientAttestationException(
        override val e: Throwable,
        override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
    ) : AppIntegrityException(e, type)

    data class ProofOfPossessionException(
        override val e: Throwable,
        override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
    ) : AppIntegrityException(e, type)

    data class Other(
        override val e: Throwable,
        override val type: AppIntegrityErrorType = AppIntegrityErrorType.INTERMITTENT
    ) : AppIntegrityException(e, type)

    enum class AppIntegrityErrorType {
        INTERMITTENT,
        APP_CHECK_FAILED,
        GENERIC
    }
}
