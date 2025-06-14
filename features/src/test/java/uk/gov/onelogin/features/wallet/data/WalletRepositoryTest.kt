package uk.gov.onelogin.features.wallet.data

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class WalletRepositoryTest {
    val sut = WalletRepositoryImpl()

    @Test
    fun `verify credential setter and getter`() {
        val expectedString = "credential"
        sut.addCredential(expectedString)
        assertEquals(expectedString, sut.getCredential())
    }

    @Test
    fun `verify deeplink path setter and getter`() {
        val expectedPath = "path"
        sut.addDeepLinkPath(expectedPath)
        assertEquals(expectedPath, sut.getDeepLinkPath())
    }

    @Test
    fun `verify deeplink path setter null`() {
        val expectedPath = ""
        sut.addDeepLinkPath(null)
        assertEquals(expectedPath, sut.getDeepLinkPath())
    }
}
