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
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelIntentTest {
    private val analyticsOptInRepo: AnalyticsOptInRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val walletRepository: WalletRepository = mock()

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        viewModel = MainActivityViewModel(
            analyticsOptInRepo,
            walletRepository,
            mockAutoInitialiseSecureStore
        )
    }

    @Test
    fun validDeepLink() {
        val credentialOffer = "xxx"
        val deeplink = "app://route?credential_offer=$credentialOffer"
        val intent =
            Intent().apply {
                action = ACTION_VIEW
                data = deeplink.toUri()
            }
        viewModel.handleIntent(intent)

        verify(walletRepository).addCredential(credentialOffer)
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
        viewModel.handleIntent(intent)

        verifyNoInteractions(walletRepository)
    }
}
