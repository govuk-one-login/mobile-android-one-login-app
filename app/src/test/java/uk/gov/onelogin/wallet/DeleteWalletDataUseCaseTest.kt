package uk.gov.onelogin.wallet

import androidx.fragment.app.FragmentActivity
import io.ktor.util.reflect.instanceOf
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.android.wallet.sdk.WalletSdkImpl
import uk.gov.onelogin.wallet.DeleteWalletDataUseCaseImpl.Companion.DELETE_WALLET_DATA_ERROR

class DeleteWalletDataUseCaseTest {
    private val walletSdk: WalletSdkImpl = mock()
    private val activityFragment: FragmentActivity = mock()
    private val sut = DeleteWalletDataUseCaseImpl(walletSdk)

    @Test
    fun `delete wallet data success`() = runBlocking {
        whenever(walletSdk.deleteWalletData(any())).thenReturn(true)
        val result = sut(activityFragment)
        assertEquals(Unit, result)
    }

    @Test
    fun `delete wallet data error`() = runBlocking {
        whenever(walletSdk.deleteWalletData(any())).thenReturn(false)
        val result = Assertions.assertThrows(Exception::class.java) {
            runBlocking { sut.invoke(activityFragment) }
        }
        assertTrue(result.message!!.contains(DELETE_WALLET_DATA_ERROR))
    }

    @Test
    fun `walled sdk init error`() = runBlocking {
        whenever(walletSdk.deleteWalletData(any())).then {
            throw WalletSdk.WalletSdkError.SdkNotInitialised
        }
        val result = Assertions.assertThrows(
            WalletSdk.WalletSdkError.SdkNotInitialised::class.java
        ) {
            runBlocking { sut.invoke(activityFragment) }
        }
        assertTrue(result.instanceOf(WalletSdk.WalletSdkError.SdkNotInitialised::class))
    }
}
