package uk.gov.onelogin.core.ui.wallet

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class WalletDisplayerImplTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val displayAsFullScreen = MutableStateFlow(false)
    private val displayer = WalletDisplayerImpl(walletSdk)

    @Before
    fun setUp() {
        whenever(walletSdk.displayAsFullScreen).thenReturn(displayAsFullScreen)
    }

    @Test
    fun `Wallet delegates to walletSdk WalletApp`() {
        composeTestRule.setContent {
            displayer.Wallet(
                onFullScreenRequest = {}
            )
            verify(walletSdk).WalletApp()
        }
    }

    @Test
    fun `it emits full screen requests`() = runTest {
        val onFullScreenRequest = mock<(Boolean) -> Unit>()

        composeTestRule.setContent {
            displayer.Wallet(
                onFullScreenRequest = onFullScreenRequest
            )
        }
        composeTestRule.awaitIdle()

        displayAsFullScreen.emit(true)
        composeTestRule.awaitIdle()

        displayAsFullScreen.emit(false)
        composeTestRule.awaitIdle()

        inOrder(onFullScreenRequest) {
            verify(onFullScreenRequest).invoke(false)
            verify(onFullScreenRequest).invoke(true)
            verify(onFullScreenRequest).invoke(false)
        }
        verifyNoMoreInteractions(onFullScreenRequest)
    }
}
