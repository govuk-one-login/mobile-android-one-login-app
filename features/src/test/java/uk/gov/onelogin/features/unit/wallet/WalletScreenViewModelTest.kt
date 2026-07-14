package uk.gov.onelogin.features.unit.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.wallet.ui.WalletScreenViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WalletScreenViewModelTest {
    private val walletSdk: WalletSdk = mock()
    private val getWalletStoreId: GetWalletStoreId = mock()
    private val logger = MemorisedLogger()
    private lateinit var viewModel: WalletScreenViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            WalletScreenViewModel(
                walletSdk,
                getWalletStoreId,
                logger
            )
    }

    @Test
    fun `sets wallet store id when getWalletStoreId returns a value`() =
        runTest {
            val storeId = "test-store-id"
            whenever(getWalletStoreId.invoke()).thenReturn(storeId)

            viewModel.setWalletStoreId()

            verify(walletSdk).setWalletStoreId(storeId)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `does not set wallet store id when getWalletStoreId returns null`() =
        runTest {
            whenever(getWalletStoreId.invoke()).thenReturn(null)

            viewModel.setWalletStoreId()

            verify(walletSdk, never()).setWalletStoreId(any())
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `logs error when getWalletStoreId throws an exception`() =
        runTest {
            whenever(getWalletStoreId.invoke()).thenThrow(RuntimeException("token error"))

            viewModel.setWalletStoreId()

            verify(walletSdk, never()).setWalletStoreId(any())
            assertThat(logger, hasItem( allOf(
                hasMessage("Could not set Wallet store ID"),
                hasException(instanceOf(WalletScreenViewModel.SetWalletStoreIdException::class.java)
                )
            )
            )
            )
        }
}
