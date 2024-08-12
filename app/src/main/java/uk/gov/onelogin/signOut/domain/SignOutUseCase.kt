package uk.gov.onelogin.signOut.domain

import javax.inject.Inject
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry

fun interface SignOutUseCase {
    operator fun invoke()
}

@Suppress("TooGenericExceptionCaught")
class SignOutUseCaseImpl @Inject constructor(
    private val removeAllSecureStoreData: RemoveAllSecureStoreData,
    private val removeTokenExpiry: RemoveTokenExpiry,
    private val bioPrefHandler: BiometricPreferenceHandler
) : SignOutUseCase {
    @Throws(SignOutError::class)
    override fun invoke() {
        try {
            removeTokenExpiry()
            removeAllSecureStoreData()
            bioPrefHandler.clear()
        } catch (e: Throwable) {
            throw SignOutError(e)
        }
    }
}

data class SignOutError(val error: Throwable) : Error()
