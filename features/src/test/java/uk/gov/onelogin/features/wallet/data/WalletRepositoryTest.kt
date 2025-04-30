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
}
