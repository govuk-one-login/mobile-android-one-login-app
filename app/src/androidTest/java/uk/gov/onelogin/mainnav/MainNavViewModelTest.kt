package uk.gov.onelogin.mainnav

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.features.wallet.data.WalletRepository
import uk.gov.onelogin.mainnav.ui.MainNavViewModel

class MainNavViewModelTest {
    private val mockWalletRepository: WalletRepository = mock()
    private val sut = MainNavViewModel(mockWalletRepository)

    @Test
    fun verifySetDisplayContentAsFullScreenState() {
        val expectedValue = true
        sut.setDisplayContentAsFullScreenState(expectedValue)
        assertEquals(expectedValue, sut.displayContentAsFullScreenState.value)
    }

    @Test
    fun verifyCheckIsDeeplinkRoutePasses() {
        whenever(mockWalletRepository.isWalletDeepLinkPath()).thenReturn(true)
        whenever(mockWalletRepository.isTokensStored()).thenReturn(true)
        sut.checkIsDeeplinkRoute()
        assertEquals(true, sut.isDeeplinkRoute.value)
    }

    @Test
    fun verifyCheckIsDeeplinkRouteFails() {
        whenever(mockWalletRepository.isWalletDeepLinkPath()).thenReturn(true)
        whenever(mockWalletRepository.isTokensStored()).thenReturn(false)
        sut.checkIsDeeplinkRoute()
        assertEquals(false, sut.isDeeplinkRoute.value)
    }
}
