package uk.gov.onelogin.wallet

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletSdk

fun interface DeleteWalletDataUseCase {
    suspend operator fun invoke(activityFragment: FragmentActivity)
}

@Suppress(
    "TooGenericExceptionCaught",
    "RethrowCaughtException",
    "TooGenericExceptionThrown"
)
class DeleteWalletDataUseCaseImpl @Inject constructor(
    private val walletSdk: WalletSdk
) : DeleteWalletDataUseCase {
    override suspend operator fun invoke(activityFragment: FragmentActivity) {
        try {
            val deleteResult = walletSdk.deleteWalletData(activityFragment)
            if (!deleteResult) throw Exception(DELETE_WALLET_DATA_ERROR)
        } catch (e: Throwable) {
            throw e
        }
    }

    companion object {
        const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
    }
}
