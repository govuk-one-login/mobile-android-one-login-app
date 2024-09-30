package uk.gov.onelogin.signOut.domain

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.delete.domain.MultiCleaner
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry
import uk.gov.onelogin.wallet.DeleteWalletDataUseCase
import uk.gov.onelogin.wallet.DeleteWalletDataUseCaseImpl
import uk.gov.onelogin.wallet.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@ExperimentalCoroutinesApi
class SignOutUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val activityFragment: FragmentActivity = mock()
    private val deleteWalletData: DeleteWalletDataUseCase = mock()
    private lateinit var useCase: SignOutUseCase

    @BeforeTest
    fun setUp() {
        useCase = SignOutUseCaseImpl(
                cleaner = { Result.success(Unit) },
                deleteWalletData
            )
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke clears all the required data`() = runTest {
        // Given
        val removeAllSecureStoreData: RemoveAllSecureStoreData = mock()
        val removeTokenExpiry: RemoveTokenExpiry = mock()
        val bioPrefHandler: BiometricPreferenceHandler = mock()
        // When we call sign out use case
        useCase =
            SignOutUseCaseImpl(
                MultiCleaner(Dispatchers.Main, removeAllSecureStoreData, removeTokenExpiry, bioPrefHandler),
                deleteWalletData
            )
        useCase.invoke(activityFragment)
        // Then it clears all the required data
        verify(removeTokenExpiry).clean()
        verify(removeAllSecureStoreData).clean()
        verify(bioPrefHandler).clean()
        verify(deleteWalletData).invoke(activityFragment)
    }

    @Test
    fun `exception propagates up as a SignOutError`() = runTest {
        // Given
        val errorMessage = "something went terribly bad"
        useCase = SignOutUseCaseImpl(
            MultiCleaner(
                Dispatchers.Main,
                { Result.success(Unit) },
                { throw Exception(errorMessage) }
            ),
            deleteWalletData
        )
        // When invoking the sign out use case
        // Then throw SignOutError
        val exception = assertThrows<SignOutError> {
            useCase.invoke(activityFragment)
        }
        assertTrue(exception.error.message!!.contains(errorMessage))
    }

    @Test
    fun `sign out delete wallet data error`() = runTest {
        // When invoking the sign out use case
        whenever(deleteWalletData.invoke(activityFragment)).then {
            throw DeleteWalletDataUseCaseImpl.DeleteWalletDataError()
        }
        // Then throw SignOutError
        val exception = assertThrows<SignOutError> {
            useCase.invoke(activityFragment)
        }
        assertTrue(exception.error.message!!.contains(DELETE_WALLET_DATA_ERROR))
    }
}
