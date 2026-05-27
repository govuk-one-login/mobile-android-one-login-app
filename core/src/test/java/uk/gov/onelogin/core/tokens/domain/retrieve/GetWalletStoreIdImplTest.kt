package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.utils.MockitoHelper

class GetWalletStoreIdImplTest {
    private val expectedWalletId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"

    private val mockGetFromOpenSecureStore: GetFromOpenSecureStore = mock()

    private val sut = GetWalletStoreIdImpl(mockGetFromOpenSecureStore)

    @Test
    fun successScenario() =
        runTest {
            whenever(mockGetFromOpenSecureStore.invoke(MockitoHelper.anyObject()))
                .thenReturn(mapOf(WALLET_ID_KEY to expectedWalletId))

            val walletId = sut.invoke()
            assertEquals(walletId, expectedWalletId)
        }

    @Test
    fun `GetWalletStoreIdImpl invoke returns null when no wallet id found`() =
        runTest {
            whenever(mockGetFromOpenSecureStore.invoke(MockitoHelper.anyObject()))
                .thenReturn(emptyMap())

            val result = sut.invoke()

            assertNull(result)
        }

    private companion object {
        private const val WALLET_ID_KEY = "wallet_id"
    }
}
