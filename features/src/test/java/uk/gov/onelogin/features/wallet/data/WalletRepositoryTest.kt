package uk.gov.onelogin.features.wallet.data

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class WalletRepositoryTest {
    val sut = WalletRepositoryImpl()

    @Test
    fun `verify setter and getter of deep link path state`() {
        assertFalse(sut.isWalletDeepLinkPath())
        sut.toggleWallDeepLinkPathState()
        assertTrue(sut.isWalletDeepLinkPath())
    }
}
