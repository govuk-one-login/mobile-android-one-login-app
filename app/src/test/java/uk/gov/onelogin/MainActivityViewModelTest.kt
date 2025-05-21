package uk.gov.onelogin

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class MainActivityViewModelTest {
    private val mockContext: Context = mock()
    private val analyticsOptInRepo: AnalyticsOptInRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockLifecycleOwner: LifecycleOwner = mock()
    private val walletRepository: WalletRepository = mock()
    private val featureFlags: FeatureFlags = mock()

    private lateinit var viewModel: MainActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = MainActivityViewModel(
            analyticsOptInRepo,
            walletRepository,
            featureFlags,
            mockAutoInitialiseSecureStore
        )
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Test
    fun `secure store auto initialised`() = runTest {
        verify(mockAutoInitialiseSecureStore).initialise()
    }

    @Test
    fun `synchronise analytics on each app start`() = runTest {
        viewModel.onStart(owner = mockLifecycleOwner)
        verify(analyticsOptInRepo).synchronise()
    }
}
