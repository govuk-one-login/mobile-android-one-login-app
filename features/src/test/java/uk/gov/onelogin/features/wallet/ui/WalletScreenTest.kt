package uk.gov.onelogin.features.wallet.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val featureFlags: FeatureFlags = mock()

    private val viewModel = WalletScreenViewModel(
        walletSdk,
        featureFlags
    )

    @Test
    fun homeScreenDisplayed() {
        composeTestRule.setContent {
            WalletScreen(viewModel)

            verify(walletSdk).WalletApp("", false)
        }
    }
}
