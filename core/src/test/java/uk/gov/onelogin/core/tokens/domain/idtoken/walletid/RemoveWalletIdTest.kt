package uk.gov.onelogin.core.tokens.domain.idtoken.walletid

import kotlinx.coroutines.test.runTest
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.RemoveWalletStoreIdImpl
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.WALLET_ID_KEY
import uk.gov.onelogin.core.tokens.domain.retrieve.FakeRemoveFromOpenSecureStore
import kotlin.test.Test
import kotlin.test.assertNull

class RemoveWalletIdTest {
    private val expectedWalletId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
    private val fakeRemoveFromOpenSecureStore = FakeRemoveFromOpenSecureStore()
    private val sut = RemoveWalletStoreIdImpl(fakeRemoveFromOpenSecureStore)

    @Test
    fun `removes wallet id from secure store`() =
        runTest {
            fakeRemoveFromOpenSecureStore.store[WALLET_ID_KEY] = expectedWalletId

            sut()

            assertNull(fakeRemoveFromOpenSecureStore.store[WALLET_ID_KEY])
        }
}
