package uk.gov.onelogin.signOut.domain

import androidx.fragment.app.FragmentActivity
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry
import uk.gov.onelogin.wallet.DeleteWalletDataUseCase
import uk.gov.onelogin.wallet.DeleteWalletDataUseCaseImpl
import uk.gov.onelogin.wallet.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR

class SignOutUseCaseTest {
    private val activityFragment: FragmentActivity = mock()
    private val removeAllSecureStoreData: RemoveAllSecureStoreData = mock()
    private val removeTokenExpiry: RemoveTokenExpiry = mock()
    private val bioPrefHandler: BiometricPreferenceHandler = mock()
    private val deleteWalletData: DeleteWalletDataUseCase = mock()

    private val sut =
        SignOutUseCaseImpl(
            removeAllSecureStoreData,
            removeTokenExpiry,
            bioPrefHandler,
            deleteWalletData
        )

    @Test
    operator fun invoke() = runBlocking {
        // WHEN we call sign out use case
        sut.invoke(activityFragment)

        // THEN it clears all the required data
        verify(removeTokenExpiry).invoke()
        verify(removeAllSecureStoreData).invoke()
        verify(bioPrefHandler).clear()
        verify(deleteWalletData).invoke(activityFragment)
    }

    @Test
    fun `sign out error`() {
        whenever(removeTokenExpiry.invoke())
            .thenThrow(RuntimeException("something went terribly bad"))

        val exception: SignOutError = Assertions.assertThrows(SignOutError::class.java) {
            runBlocking { sut.invoke(activityFragment) }
        }
        assertTrue(exception.error.message!!.contains("something went terribly bad"))
    }

    @Test
    fun `sign out delete wallet data error`() = runBlocking {
        whenever(deleteWalletData.invoke(activityFragment)).then {
            throw DeleteWalletDataUseCaseImpl.DeleteWalletDataError()
        }
        val exception: SignOutError = Assertions.assertThrows(SignOutError::class.java) {
            runBlocking { sut.invoke(activityFragment) }
        }
        assertTrue(exception.error.message!!.contains(DELETE_WALLET_DATA_ERROR))
    }
}
