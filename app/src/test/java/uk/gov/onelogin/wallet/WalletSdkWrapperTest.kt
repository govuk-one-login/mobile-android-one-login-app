package uk.gov.onelogin.wallet

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository
import uk.gov.onelogin.wallet.mock.WalletMock

class WalletSdkWrapperTest {
    private val realWalletSdk: WalletSdk = mock()
    private val mockWalletSdk: WalletMock = mock()
    private val devOptionsRepository: SecureStoreDevOptionsRepository = mock()
    private lateinit var wrapper: WalletSdkWrapper

    @BeforeEach
    fun setUp() {
        wrapper = WalletSdkWrapper(realWalletSdk, mockWalletSdk, devOptionsRepository)
    }

    @Test
    fun `delegates to mock when mock override is true`() {
        whenever(devOptionsRepository.isWalletMockOverride()).thenReturn(true)
        val config: WalletSdk.ConfigurationV2 = mock()
        whenever(mockWalletSdk.config).thenReturn(config)

        assertEquals(config, wrapper.config)
        verify(mockWalletSdk).config
    }

    @Test
    fun `delegates to real when mock override is false`() {
        whenever(devOptionsRepository.isWalletMockOverride()).thenReturn(false)
        val config: WalletSdk.ConfigurationV2 = mock()
        whenever(realWalletSdk.config).thenReturn(config)

        assertEquals(config, wrapper.config)
        verify(realWalletSdk).config
    }

    @Test
    fun `isEmpty delegates correctly`() {
        whenever(devOptionsRepository.isWalletMockOverride()).thenReturn(true)
        whenever(mockWalletSdk.isEmpty()).thenReturn(true)

        assertTrue(wrapper.isEmpty())
        verify(mockWalletSdk).isEmpty()
    }

    @Test
    fun `setWalletStoreId delegates correctly`() {
        whenever(devOptionsRepository.isWalletMockOverride()).thenReturn(false)

        wrapper.setWalletStoreId("store-123")
        verify(realWalletSdk).setWalletStoreId("store-123")
    }

    @Test
    fun `deleteWalletData delegates correctly`() = runTest {
        whenever(devOptionsRepository.isWalletMockOverride()).thenReturn(true)
        whenever(mockWalletSdk.deleteWalletData()).thenReturn(true)

        val result = wrapper.deleteWalletData()
        assertTrue(result)
        verify(mockWalletSdk).deleteWalletData()
    }
}
