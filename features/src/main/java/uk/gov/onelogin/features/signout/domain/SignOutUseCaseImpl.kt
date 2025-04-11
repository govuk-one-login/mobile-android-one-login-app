package uk.gov.onelogin.features.signout.domain

import javax.inject.Inject
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase

@Suppress("TooGenericExceptionCaught")
class SignOutUseCaseImpl @Inject constructor(
    private val cleaner: Cleaner,
    private val deleteWalletData: DeleteWalletDataUseCase
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override suspend fun invoke() {
        try {
            cleaner.clean()
            deleteWalletData.invoke()
        } catch (e: Throwable) {
            throw SignOutError(e)
        }
    }
}

data class SignOutError(val error: Throwable) : Error()
