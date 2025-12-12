package uk.gov.onelogin.features.wallet.data

import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class WalletRepositoryTest {
    val sut = WalletRepositoryImpl()

    @Test
    fun `verify setter and getter of deep link path state`() {
        sut.setWalletDeepLinkPathState(true)
        assertTrue(sut.isWalletDeepLinkPath())
    }
}
