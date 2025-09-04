package uk.gov.onelogin.features.wallet.domain

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository

class DeleteWalletDataUseCaseDevOptionTest {
    private val walletSdk: WalletSdk = mock()
    private val secureStoreDevOptionsRepository: SecureStoreDevOptionsRepository = mock()
    private val sut = DeleteWalletDataUseCaseDevOption(walletSdk, secureStoreDevOptionsRepository)

    @Test
    fun verifyOverrideDisabled() = runBlocking {
        whenever(secureStoreDevOptionsRepository.isWalletDeleteOverride()).thenReturn(false)
        val expectedResult = true
        whenever(walletSdk.deleteWalletData()).thenReturn(expectedResult)

        val actualResult = sut.invoke()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun verifyOverrideEnabled() {
        whenever(secureStoreDevOptionsRepository.isWalletDeleteOverride()).thenReturn(true)

        Assertions.assertThrows(
            SecureStorageError::class.java
        ) {
            runBlocking { sut.invoke() }
        }
    }
}
