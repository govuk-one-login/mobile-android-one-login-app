package uk.gov.onelogin.wallet.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.android.wallet.sdk.WalletSdk

class WalletMockTest {
    private val config: WalletSdk.ConfigurationV2 = mock()
    private val displayAsFullScreen = MutableStateFlow(false)
    private lateinit var walletMock: WalletMock

    @BeforeEach
    fun setUp() {
        walletMock = WalletMock(config, displayAsFullScreen)
    }

    @Test
    fun `initial state is empty`() {
        assertTrue(walletMock.isEmpty())
        assertEquals(config, walletMock.config)
        assertEquals(displayAsFullScreen, walletMock.displayAsFullScreen)
    }

    @Test
    fun `setting wallet store id updates empty state`() {
        walletMock.setWalletStoreId("test-store-id")
        assertFalse(walletMock.isEmpty())
    }

    @Test
    fun `deleting wallet data resets empty state and returns true`() = runTest {
        walletMock.setWalletStoreId("test-store-id")
        assertFalse(walletMock.isEmpty())

        val result = walletMock.deleteWalletData()
        assertTrue(result)
        assertTrue(walletMock.isEmpty())
    }

    @Test
    fun `deleteWalletData increments call count`() = runTest {
        assertEquals(0, walletMock.deleteCallCount.value)
        walletMock.deleteWalletData()
        assertEquals(1, walletMock.deleteCallCount.value)
        walletMock.deleteWalletData()
        assertEquals(2, walletMock.deleteCallCount.value)
    }
}
