package uk.gov.onelogin.features.signout.domain

import javax.inject.Inject
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.features.wallet.domain.DelegateDeleteWalletUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl
import uk.gov.onelogin.features.wallet.domain.DeleteWalletMock

@Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
class SignOutUseCaseImpl
@Inject
constructor(
    private val cleaner: Cleaner,
    private val delegateDeleteWalletUseCase: DelegateDeleteWalletUseCase,
    private val tokenRepository: TokenRepository,
    private val logger: Logger,
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override suspend fun invoke() {
        try {
            val isDeleted = delegateDeleteWalletUseCase.invoke()
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
        } catch (e: DeleteWalletMock.DeleteWalletMockError) {
            throw e
        } catch (e: Throwable) {
            logError(e)
            throw SignOutError(e)
        }
    }

    private fun logError(e: Throwable) {
        logger.error(
            this.javaClass.simpleName,
            e.message ?: DEFAULT_ERROR_MSG,
            e,
        )
    }

    companion object {
        private const val DEFAULT_ERROR_MSG =
            "Sign out error was caught when attempting to delete user data!"
    }
}

data class SignOutError(val error: Throwable) : Error(error)
