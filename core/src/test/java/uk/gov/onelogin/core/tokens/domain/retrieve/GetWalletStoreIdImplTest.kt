package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GetWalletStoreIdImplTest {
    private val expectedWalletId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"

    private val fakeGetFromOpenSecureStore = FakeGetFromOpenSecureStore()

    private val sut = GetWalletStoreIdImpl(fakeGetFromOpenSecureStore)

    @Test
    fun successScenario() =
        runTest {
            fakeGetFromOpenSecureStore["wallet_id"] = expectedWalletId

            val walletId = sut.invoke()
            assertEquals(expectedWalletId, walletId)
        }

    @Test
    fun `GetWalletStoreIdImpl invoke returns empty when wallet id is empty`() =
        runTest {
            fakeGetFromOpenSecureStore["wallet_id"] = ""

            val result = sut.invoke()
            assertEquals("", result)
        }

    @Test
    fun `GetWalletStoreIdImpl invoke returns null when store returns null`() =
        runTest {
            val result = sut.invoke()
            assertNull(result)
        }
}
