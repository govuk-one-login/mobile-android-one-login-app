package uk.gov.onelogin.signOut.domain

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry
import uk.gov.onelogin.wallet.DeleteWalletDataUseCase

fun interface SignOutUseCase {
    suspend operator fun invoke(activityFragment: FragmentActivity)
}

@Suppress("TooGenericExceptionCaught")
class SignOutUseCaseImpl @Inject constructor(
    private val removeAllSecureStoreData: RemoveAllSecureStoreData,
    private val removeTokenExpiry: RemoveTokenExpiry,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val deleteWalletData: DeleteWalletDataUseCase
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override suspend fun invoke(activityFragment: FragmentActivity) {
        try {
            removeTokenExpiry()
            removeAllSecureStoreData()
            bioPrefHandler.clear()
            deleteWalletData(activityFragment)
        } catch (e: Throwable) {
            throw SignOutError(e)
        }
    }
}

data class SignOutError(val error: Throwable) : Error()
