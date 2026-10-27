package uk.gov.onelogin.features.unit.signout.domain

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.onelogin.core.cleaner.domain.MultiCleaner
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiry
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import uk.gov.onelogin.features.signout.domain.SignOutUseCaseImpl
import uk.gov.onelogin.features.wallet.domain.DelegateDeleteWalletUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignOutUseCaseTest {
    private val deleteWalletData: DelegateDeleteWalletUseCase = mock()
    private val tokenRepository: TokenRepository = mock()
    private val logger = MemorisedLogger()
    private lateinit var useCase: SignOutUseCase

    @Test
    fun `invoke clears all the required data`() = runTest {
        // Given
        val removeAllSecureStoreData: RemoveAllSecureStoreData = mock()
        val removeTokenExpiry: RemoveTokenExpiry = mock()
        val bioPrefHandler: LocalAuthPreferenceRepo = mock()

        whenever(deleteWalletData.invoke()).thenReturn(true)
        // When we call sign out use case
        useCase =
            SignOutUseCaseImpl(
                MultiCleaner(
                    Dispatchers.Main,
                    removeAllSecureStoreData,
                    removeTokenExpiry,
                    bioPrefHandler,
                ),
                deleteWalletData,
                tokenRepository,
                logger,
            )
        useCase.invoke()
        // Then it clears all the required data
        verify(removeTokenExpiry).clean()
        verify(removeAllSecureStoreData).clean()
        verify(bioPrefHandler).clean()
        verify(deleteWalletData).invoke()
        verify(tokenRepository).clearTokenResponse()
    }

    @Test
    fun `sign out delete wallet data error`() = runTest {
        // When invoking the sign out use case
        whenever(deleteWalletData.invoke()).thenReturn(false)

        useCase =
            SignOutUseCaseImpl(
                MultiCleaner(
                    Dispatchers.Main,
                    { Result.success(Unit) },
                ),
                deleteWalletData,
                tokenRepository,
                logger,
            )

        // Then throw SignOutWalletError
        val exception =
            assertThrows<SignOutError> {
                useCase.invoke()
            }
        assertTrue(exception.error.message!!.contains(DELETE_WALLET_DATA_ERROR))
    }
}
