package uk.gov.onelogin.features.signout.domain

import javax.inject.Inject
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase

@Suppress("TooGenericExceptionCaught")
class SignOutUseCaseImpl @Inject constructor(
    private val cleaner: Cleaner,
    private val deleteWalletData: DeleteWalletDataUseCase,
    private val tokenRepository: TokenRepository
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override suspend fun invoke() {
        try {
            deleteWalletData.invoke()
            tokenRepository.clearTokenResponse()

            val result = cleaner.clean()
            if (result.isFailure) {
                result.exceptionOrNull()?.let {
                    throw it
                }
            }
        } catch (e: Throwable) {
            throw SignOutError(e)
        }
    }
}

data class SignOutError(val error: Throwable) : Error(error)
