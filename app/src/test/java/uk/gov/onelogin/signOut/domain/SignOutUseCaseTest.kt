package uk.gov.onelogin.signOut.domain

import androidx.fragment.app.FragmentActivity
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry

class SignOutUseCaseTest {
    private val removeAllSecureStoreData: RemoveAllSecureStoreData = mock()
    private val removeTokenExpiry: RemoveTokenExpiry = mock()
    private val bioPrefHandler: BiometricPreferenceHandler = mock()
    private val fragmentActivity: FragmentActivity = mock()

    private val sut =
        SignOutUseCaseImpl(removeAllSecureStoreData, removeTokenExpiry, bioPrefHandler)

    @Test
    operator fun invoke() {
        // WHEN we call sign out use case
        sut.invoke(fragmentActivity)

        // THEN it clears all the required data
        verify(removeTokenExpiry).invoke()
        verify(removeAllSecureStoreData).invoke(fragmentActivity)
        verify(bioPrefHandler).clear()
    }

    @Test
    fun `sign out error`() {
        whenever(removeTokenExpiry.invoke())
            .thenThrow(RuntimeException("something went terribly bad"))

        val exception: SignOutError = Assertions.assertThrows(SignOutError::class.java) {
            sut.invoke(fragmentActivity)
        }
        assertTrue(exception.error.message!!.contains("something went terribly bad"))
    }
}
