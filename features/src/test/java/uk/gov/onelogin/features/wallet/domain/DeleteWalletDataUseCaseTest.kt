package uk.gov.onelogin.features.wallet.domain

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.android.wallet.sdk.WalletSdkImpl
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteWalletDataUseCaseTest {
    private val walletSdk: WalletSdkImpl = mock()
    private val sut = DeleteWalletDataUseCaseImpl(walletSdk)

    @Test
    fun `delete wallet data success`() =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).thenReturn(true)
            val result = sut.invoke()
            assertEquals(Unit, result)
        }

    @Test
    fun `delete wallet data error`() =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).thenReturn(false)
            val result =
                Assertions.assertThrows(
                    DeleteWalletDataUseCaseImpl.DeleteWalletDataError::class.java
                ) {
                    runBlocking { sut.invoke() }
                }
            assertTrue(result.message.contains(DELETE_WALLET_DATA_ERROR))
        }

    @Test
    fun `walled sdk init error`(): Unit =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).then {
                throw WalletSdk.WalletSdkError.SdkNotInitialised
            }
            Assertions.assertThrows(
                WalletSdk.WalletSdkError.SdkNotInitialised::class.java
            ) {
                runBlocking { sut.invoke() }
            }
        }
}
