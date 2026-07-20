package uk.gov.onelogin.core.ui.wallet

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class WalletAppDisplayerImplTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()

    @Test
    fun `WalletApp delegates to walletSdk WalletApp`() {
        val displayer = WalletAppDisplayerImpl(walletSdk)

        composeTestRule.setContent {
            displayer.WalletApp()
            verify(walletSdk).WalletApp()
        }
    }
}
