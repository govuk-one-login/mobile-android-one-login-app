package uk.gov.onelogin.signOut.domain

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry

fun interface SignOutUseCase {
    operator fun invoke(context: FragmentActivity)
}

@Suppress("TooGenericExceptionCaught")
class SignOutUseCaseImpl @Inject constructor(
    private val removeAllSecureStoreData: RemoveAllSecureStoreData,
    private val removeTokenExpiry: RemoveTokenExpiry,
    private val bioPrefHandler: BiometricPreferenceHandler
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override fun invoke(context: FragmentActivity) {
        try {
            removeTokenExpiry()
            removeAllSecureStoreData(context)
            bioPrefHandler.clear()
        } catch (e: Throwable) {
            throw SignOutError(e)
        }
    }
}

data class SignOutError(val error: Throwable) : Error()
