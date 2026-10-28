package uk.gov.onelogin.features.wallet.domain

import javax.inject.Inject
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository

fun interface DelegateDeleteWalletUseCase {
    suspend fun invoke(): Boolean
}

/**
 * Wrapper implementation of [DeleteWalletDataUseCase] that dynamically delegates to either the real use case
 * or a mock use case based on whether mock wallet override is enabled.
 */
class DeleteWalletDataUseCaseWrapper @Inject constructor(
    private val realUseCase: DeleteWalletDataUseCase,
    private val mockUseCase: DeleteMockWalletDataUseCase,
    private val devOptionsRepository: SecureStoreDevOptionsRepository,
) : DelegateDeleteWalletUseCase {

    override suspend fun invoke(): Boolean = if (devOptionsRepository.isWalletMockOverride()) {
        mockUseCase.invoke()
    } else {
        realUseCase.invoke()
    }
}
