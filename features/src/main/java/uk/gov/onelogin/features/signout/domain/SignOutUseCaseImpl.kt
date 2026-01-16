package uk.gov.onelogin.features.signout.domain

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
                    throw DeleteWalletDataUseCaseImpl.DeleteWalletDataError()
                }
            } catch (e: Throwable) {
                throw SignOutError(e)
            }
        }
    }

data class SignOutError(
    val error: Throwable,
) : Error(error)
