package uk.gov.onelogin.libraries.mockwalletsdk.unit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.libraries.mockwalletsdk.MockWalletSdk

class MockWalletSdkTest {
    private val config: WalletSdk.ConfigurationV2 = mock()
    private val displayAsFullScreen = MutableStateFlow(false)
    private val mockWalletSdk: MockWalletSdk = MockWalletSdk(config, displayAsFullScreen)

    @Test
    fun `initial state is empty`() {
        assertTrue(mockWalletSdk.isEmpty())
        assertEquals(config, mockWalletSdk.config)
        assertEquals(displayAsFullScreen, mockWalletSdk.displayAsFullScreen)
    }

    @Test
    fun `setting wallet store id updates empty state`() {
        mockWalletSdk.setWalletStoreId("test-store-id")
        assertFalse(mockWalletSdk.isEmpty())
    }

    @Test
    fun `clearing wallet store id resets empty state`() {
        mockWalletSdk.setWalletStoreId("")
        assertTrue(mockWalletSdk.isEmpty())
    }

    @Test
    fun `deleting wallet data resets empty state and returns true`() = runTest {
        mockWalletSdk.setWalletStoreId("test-store-id")
        assertFalse(mockWalletSdk.isEmpty())

        val result = mockWalletSdk.deleteWalletData()
        assertTrue(result)
        assertTrue(mockWalletSdk.isEmpty())
    }
}
