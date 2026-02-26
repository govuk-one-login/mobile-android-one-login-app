package uk.gov.onelogin.features.signout.domain

import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
class SignOutUseCaseImpl
    @Inject
    constructor(
        private val cleaner: Cleaner,
        private val deleteWalletData: DeleteWalletDataUseCase,
        private val tokenRepository: TokenRepository,
        private val logger: Logger,
    ) : SignOutUseCase {
        @Throws(SignOutError::class)
        override suspend fun invoke() {
            try {
                val isDeleted = deleteWalletData.invoke()
                if (isDeleted) {
                    val result = cleaner.clean()
                    if (result.isFailure) {
                        result.exceptionOrNull()?.let {
                            throw it
                        }
                    }
                    tokenRepository.clearTokenResponse()
                } else {
                    val error = DeleteWalletDataUseCaseImpl.DeleteWalletDataError()
                    logError(error)
                    throw error
                }
            } catch (e: Throwable) {
                logError(e)
                throw SignOutError(e)
            }
        }

        private fun logError(e: Throwable) {
            logger.error(
                this.javaClass.simpleName,
                e.message ?: DEFAULT_ERROR_MSG,
                e
            )
        }

        companion object {
            private const val DEFAULT_ERROR_MSG = "Sign out error was caught when attempting to delete user data!"
        }
    }

data class SignOutError(
    val error: Throwable,
) : Error(error)
