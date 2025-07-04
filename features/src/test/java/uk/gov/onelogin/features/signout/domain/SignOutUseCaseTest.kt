package uk.gov.onelogin.features.signout.domain

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
import uk.gov.onelogin.core.cleaner.domain.MultiCleaner
import uk.gov.onelogin.core.cleaner.domain.ResultCollectionUtil
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiry
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignOutUseCaseTest {
    private val deleteWalletData: DeleteWalletDataUseCase = mock()
    private val tokenRepository: TokenRepository = mock()
    private lateinit var useCase: SignOutUseCase

    @Test
    fun `invoke clears all the required data`() =
        runTest {
            // Given
            val removeAllSecureStoreData: RemoveAllSecureStoreData = mock()
            val removeTokenExpiry: RemoveTokenExpiry = mock()
            val bioPrefHandler: LocalAuthPreferenceRepo = mock()
            // When we call sign out use case
            useCase =
                SignOutUseCaseImpl(
                    MultiCleaner(
                        Dispatchers.Main,
                        removeAllSecureStoreData,
                        removeTokenExpiry,
                        bioPrefHandler
                    ),
                    deleteWalletData,
                    tokenRepository
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
    fun `sign out delete wallet data error`() =
        runTest {
            // When invoking the sign out use case
            whenever(deleteWalletData.invoke()).then {
                throw DeleteWalletDataUseCaseImpl.DeleteWalletDataError()
            }

            useCase =
                SignOutUseCaseImpl(
                    MultiCleaner(
                        Dispatchers.Main,
                        { Result.success(Unit) }
                    ),
                    deleteWalletData,
                    tokenRepository
                )

            // Then throw SignOutError
            val exception =
                assertThrows<SignOutError> {
                    useCase.invoke()
                }
            assertTrue(exception.error.message!!.contains(DELETE_WALLET_DATA_ERROR))
        }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `exception propagates up as a SignOutError`() =
        runTest {
            // Given
            val errorMessage = "something went terribly bad"
            useCase =
                SignOutUseCaseImpl(
                    MultiCleaner(
                        Dispatchers.Main,
                        { Result.success(Unit) },
                        { throw Exception(errorMessage) }
                    ),
                    deleteWalletData,
                    tokenRepository
                )
            // When invoking the sign out use case
            // Then throw SignOutError
            val exception =
                assertThrows<SignOutError> {
                    useCase.invoke()
                }
            assertTrue(exception.error.message!!.contains(errorMessage))
        }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `exception propagates up as a SignOutError via failure`() =
        runTest {
            // Given
            val errorMessage = "something went terribly bad"
            useCase =
                SignOutUseCaseImpl(
                    MultiCleaner(
                        Dispatchers.Main,
                        { Result.success(Unit) },
                        { Result.failure(Exception(errorMessage)) }
                    ),
                    deleteWalletData,
                    tokenRepository
                )
            // When invoking the sign out use case
            // Then throw SignOutError
            val exception =
                assertThrows<SignOutError> {
                    useCase.invoke()
                }
            assertTrue(exception.error is ResultCollectionUtil.Failure)
        }
}
