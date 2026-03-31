package uk.gov.onelogin.features.unit.wallet.data

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import uk.gov.onelogin.features.wallet.data.WalletRepositoryImpl
import kotlin.test.assertTrue

class WalletRepositoryTest {
    val sut = WalletRepositoryImpl()

    @Test
    fun `verify setter and getter of deep link path state`() {
        sut.setWalletDeepLinkPathState(true)
        assertTrue(sut.isWalletDeepLinkPath())
        sut.setWalletDeepLinkPathState(false)
        assertFalse(sut.isWalletDeepLinkPath())
    }
}
