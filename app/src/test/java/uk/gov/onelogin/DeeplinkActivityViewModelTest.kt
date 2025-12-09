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
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class DeeplinkActivityViewModelTest {
    private val mockContext: Context = mock()
    private val analyticsOptInRepo: AnalyticsOptInRepository = mock()
    private val mockLifecycleOwner: LifecycleOwner = mock()
    private val mockWalletRepository: WalletRepository = mock()
    private val mockWalletSdk: WalletSdk = mock()

    private lateinit var viewModel: DeeplinkActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = DeeplinkActivityViewModel(
            analyticsOptInRepo,
            mockWalletRepository,
            mockWalletSdk
        )
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Test
    fun `synchronise analytics on each app start`() = runTest {
        viewModel.onStart(owner = mockLifecycleOwner)
        verify(analyticsOptInRepo).synchronise()
    }
}
