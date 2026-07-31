package uk.gov.onelogin.features.unit.wallet

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayerImpl
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.wallet.ui.WalletScreenState
import uk.gov.onelogin.features.wallet.ui.WalletScreenViewModel
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class WalletScreenViewModelTest {
    @JvmField
    @RegisterExtension
    val coroutinesTestExtension = CoroutinesTestExtension()

    private val mainScheduler get() = coroutinesTestExtension.dispatcher.scheduler

    private val walletSdk: WalletSdk = mock()
    private val getWalletStoreId: GetWalletStoreId = {
        delay(loadingTime)
        WALLET_STORE_ID
    }
    private val walletAppDisplayer = WalletAppDisplayerImpl(walletSdk)

    private val viewModel by lazy {
        WalletScreenViewModel(
            walletSdk,
            getWalletStoreId,
            walletAppDisplayer,
        )
    }

    @Test
    fun `initial state is Loading before wallet store ID is set`() = runTest {
        viewModel.state.test {
            assertEquals(WalletScreenState.Loading, awaitItem())

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `wallet is available to display after loading is finished`() = runTest {
        viewModel.state.test {
            skipItems(1) // Loading
            mainScheduler.advanceTimeBy(loadingTime)
            mainScheduler.runCurrent()

            assertIs<WalletScreenState.Display>(awaitItem())
        }
    }

    @Test
    fun `setWalletStoreId is called on Wallet SDK`() =
        runTest {
            viewModel.state.test {
                skipItems(1) // Loading
                mainScheduler.advanceTimeBy(loadingTime)
                mainScheduler.runCurrent()

                verify(walletSdk).setWalletStoreId(WALLET_STORE_ID)
                skipItems(1) // Display
            }
        }

    @Test
    fun `crashes with IllegalArgumentException when wallet store ID is null`() {

        val thrown = assertThrows<Exception> {
            runTest {
                WalletScreenViewModel(
                    walletSdk = walletSdk,
                    getWalletStoreId = { null },
                    walletAppDisplayer = walletAppDisplayer,
                )
                mainScheduler.advanceUntilIdle()
            }
        }

        val cause = thrown.suppressedExceptions
            .filterIsInstance<IllegalArgumentException>()
            .firstOrNull()
            ?: assertIs<IllegalArgumentException>(thrown)

        assertEquals(
            "Wallet Store ID must not be null. This is guaranteed by ValidateWalletStoreId",
            cause.message,
        )
    }

    companion object {
        val loadingTime = 1.seconds
        const val WALLET_STORE_ID = "test-store-id"
    }
}
