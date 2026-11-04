package uk.gov.onelogin.mockwalletsdk

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.android.wallet.sdk.WalletSdk

class MockWalletSdkTest {
    private val config: WalletSdk.ConfigurationV2 = mock()
    private val displayAsFullScreen = MutableStateFlow(false)
    private lateinit var mockWalletSdk: MockWalletSdk

    @BeforeEach
    fun setUp() {
        mockWalletSdk = MockWalletSdk(config, displayAsFullScreen)
    }

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

    @Test
    fun `deleteWalletData increments call count`() = runTest {
        assertEquals(0, mockWalletSdk.deleteCallCount.value)
        mockWalletSdk.deleteWalletData()
        assertEquals(1, mockWalletSdk.deleteCallCount.value)
        mockWalletSdk.deleteWalletData()
        assertEquals(2, mockWalletSdk.deleteCallCount.value)
    }

    @Test
    fun `deleteWalletData emits withDeleteException state when shouldDeleteWalletDataException is true`() =
        runTest {
            mockWalletSdk.setWalletStoreId("test-store-id")
            mockWalletSdk.shouldDeleteWalletDataException = true

            val result = mockWalletSdk.deleteWalletData()
            assertTrue(result)
            assertTrue(mockWalletSdk.isEmpty())
            assertEquals(1, mockWalletSdk.deleteCallCount.value)
            assertTrue(mockWalletSdk.withDeleteException.value.first is MockWalletDeletionException)
            assertTrue {
                mockWalletSdk.withDeleteException.value.first.message ==
                    "Mock wallet deletion triggered navigation to mock UI"
            }
            assertTrue(mockWalletSdk.withDeleteException.value.second)
        }

    @Test
    fun `deleteWalletData emits customDeletionException when set`() = runTest {
        val customError = IllegalStateException("mock wallet deletion error")
        mockWalletSdk.shouldDeleteWalletDataException = true
        mockWalletSdk.customDeletionException = customError

        val result = mockWalletSdk.deleteWalletData()
        assertTrue(result)
        assertEquals(customError, mockWalletSdk.withDeleteException.value.first)
        assertTrue(mockWalletSdk.withDeleteException.value.second)
    }

    @Test
    fun `deleteWalletData returns true when shouldDeleteWalletDataException is false`() = runTest {
        val mockWalletSdk =
            MockWalletSdk(
                config,
                displayAsFullScreen,
                shouldDeleteWalletDataException = false,
            )
        val result = mockWalletSdk.deleteWalletData()
        mockWalletSdk.customDeletionException = null
        assertTrue(result)
    }
}
