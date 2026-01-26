package uk.gov.onelogin.features.wallet.domain

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdkImpl
import kotlin.test.assertEquals

class DeleteWalletDataUseCaseTest {
    private val walletSdk: WalletSdkImpl = mock()
    private val sut = DeleteWalletDataUseCaseImpl(walletSdk)

    @Test
    fun `delete wallet data success`() =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).thenReturn(true)
            val result = sut.invoke()
            assertEquals(true, result)
        }

    @Test
    fun `delete wallet data error`() =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).thenReturn(false)
            val result = sut.invoke()
            assertEquals(false, result)
        }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `walled sdk init error`(): Unit =
        runBlocking {
            whenever(walletSdk.deleteWalletData()).then {
                throw Error("Error")
            }
            Assertions.assertThrows(
                Error::class.java
            ) {
                runBlocking { sut.invoke() }
            }
        }
}
