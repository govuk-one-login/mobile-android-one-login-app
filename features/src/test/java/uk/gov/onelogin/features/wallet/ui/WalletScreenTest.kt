package uk.gov.onelogin.features.wallet.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val featureFlags: FeatureFlags = mock()
    private val walletRepository: WalletRepository = mock()

    private val viewModel = WalletScreenViewModel(
        walletSdk,
        featureFlags,
        walletRepository
    )

    @Ignore("Fix mockito verify composable extra arguments issue")
    @Test
    fun homeScreenDisplayed() {
        val deeplink = ""
        whenever(walletRepository.getCredential()).thenReturn(deeplink)
        composeTestRule.setContent {
            WalletScreen(
                displayContentAsFullScreen = { true },
                viewModel = viewModel
            )

            verify(walletSdk).WalletApp(deeplink)
        }
    }

    @Ignore("Fix mockito verify composable extra arguments issue")
    @Test
    fun walletSdkCalledWithDeeplink() {
        val deeplink = "credential"
        whenever(walletRepository.getCredential()).thenReturn(deeplink)
        composeTestRule.setContent {
            WalletScreen(
                displayContentAsFullScreen = { true },
                viewModel = viewModel
            )

            verify(walletSdk).WalletApp(deeplink)
        }
    }
}
