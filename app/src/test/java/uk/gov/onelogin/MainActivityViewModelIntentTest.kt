package uk.gov.onelogin

import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.ACTION_VIEW
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelIntentTest {
    private val analyticsOptInRepo: AnalyticsOptInRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val walletRepository: WalletRepository = mock()
    private val featureFlags: FeatureFlags = mock()

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        viewModel = MainActivityViewModel(
            analyticsOptInRepo,
            walletRepository,
            featureFlags,
            mockAutoInitialiseSecureStore
        )
    }

    @Test
    fun validDeepLinkWalletFeatureFlagEnabled() {
        val credentialOffer = "xxx"
        val deeplink = "app://route?credential_offer=$credentialOffer"
        val intent =
            Intent().apply {
                action = ACTION_VIEW
                data = deeplink.toUri()
            }
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        viewModel.handleIntent(intent)

        verify(walletRepository).addCredential(credentialOffer)
    }

    @Test
    fun validDeepLinkWalletFeatureFlagDisabled() {
        val credentialOffer = "xxx"
        val deeplink = "app://route?credential_offer=$credentialOffer"
        val intent =
            Intent().apply {
                action = ACTION_VIEW
                data = deeplink.toUri()
            }
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        viewModel.handleIntent(intent)

        verifyNoInteractions(walletRepository)
    }

    @Test
    fun invalidDeepLink() {
        val credentialOffer = "xxx"
        val deeplink = "https://mobile.build.account.gov.uk/wallet/add?invalid=$credentialOffer"
        val intent =
            Intent().apply {
                action = ACTION_VIEW
                data = deeplink.toUri()
            }
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        viewModel.handleIntent(intent)

        verify(walletRepository).addDeepLinkPath("/wallet/add")
    }

    @Test
    fun nullIntentData() {
        val intent =
            Intent().apply {
                action = ACTION_VIEW
                data = null
            }
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        viewModel.handleIntent(intent)

        verifyNoInteractions(walletRepository)
    }

    @Test
    fun invalidIntentAction() {
        val credentialOffer = "xxx"
        val deeplink = "app://route?credential_offer=$credentialOffer"
        val intent =
            Intent().apply {
                action = ACTION_MAIN
                data = deeplink.toUri()
            }
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        viewModel.handleIntent(intent)

        verifyNoInteractions(walletRepository)
    }
}
