package uk.gov.onelogin.features.unit.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayer
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.wallet.ui.WalletScreenState
import uk.gov.onelogin.features.wallet.ui.WalletScreenViewModel
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WalletScreenViewModelTest {
    private val walletSdk: WalletSdk = mock()
    private val getWalletStoreId: GetWalletStoreId = mock()
    private val walletAppDisplayer: WalletAppDisplayer = mock()

    private fun createViewModel(scheduler: TestCoroutineScheduler) = WalletScreenViewModel(
        walletSdk,
        getWalletStoreId,
        walletAppDisplayer,
        StandardTestDispatcher(scheduler)
    )

    @Test
    fun `initial state is Loading before wallet store ID is set`() = runTest {
        whenever(getWalletStoreId.invoke()).thenReturn(storeId)

        val viewModel = createViewModel(testScheduler)

        assertIs<WalletScreenState.Loading>(viewModel.state.value)
    }

    @Test
    fun `verify setWalletStoreId is called and state is Display after init when store ID is returned`() = runTest {
        whenever(getWalletStoreId.invoke()).thenReturn(storeId)

        val viewModel = createViewModel(testScheduler)

        advanceUntilIdle()

        verify(walletSdk).setWalletStoreId(storeId)
        assertIs<WalletScreenState.Display>(viewModel.state.value)
    }

    companion object {
        const val storeId = "test-store-id"
    }
}
