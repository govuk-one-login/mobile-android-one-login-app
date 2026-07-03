package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasCustomKeys
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasLogEntry
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.MockitoHelper
import kotlin.test.assertNull

class GetPersistentIdImplTest {
    private val expectedPersistentId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
    private val mockGetFromOpenSecureStore: GetFromOpenSecureStore = mock()
    private val walletSdk: WalletSdk = mock()

    private val logger = MemorisedLogger()

    private val componentKey = componentKey("tokens.persistent_id")
    private val actionKey = actionKey("Get persistent ID")

    private val sut =
        GetPersistentIdImpl(
            mockGetFromOpenSecureStore,
            walletSdk = walletSdk,
            logger = logger
        )

    @Test
    fun successScenario() =
        runTest {
            whenever(mockGetFromOpenSecureStore.invoke(MockitoHelper.anyObject()))
                .thenReturn(mapOf(AuthTokenStoreKeys.PERSISTENT_ID_KEY to expectedPersistentId))
            val idResponse = sut.invoke()

            assertEquals(expectedPersistentId, idResponse)
        }

    @Test
    fun `success scenario is empty true`() =
        runTest {
            whenever(mockGetFromOpenSecureStore.invoke(MockitoHelper.anyObject()))
                .thenReturn(null)
            whenever(walletSdk.isEmpty()).thenReturn(
                true
            )
            val idResponse = sut.invoke()

            assertEquals(null, idResponse)
        }

    @Test
    fun missingToken() =
        runTest {
            // Given token is null
            whenever(
                mockGetFromOpenSecureStore(eq(AuthTokenStoreKeys.PERSISTENT_ID_KEY)),
            ).thenReturn(
                null,
            )

            val idResponse = sut.invoke()
            assertNull(idResponse)
        }

    @Test
    fun `check if logger logs when wallet is not empty and persistent id is null`() {
        runTest {
            whenever(
                walletSdk.isEmpty()
            ).thenReturn(false)

            // Given token is null
            whenever(
                mockGetFromOpenSecureStore(eq(AuthTokenStoreKeys.PERSISTENT_ID_KEY)),
            ).thenReturn(
                null,
            )

            sut.invoke()

            assertThat(logger, hasSize(1))
            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage("No persistent ID but wallet isn't empty"),
                            hasException(instanceOf(GetPersistentIdException.WalletNotEmpty::class.java)),
                            hasCustomKeys(
                                contains(
                                    equalTo(componentKey),
                                    equalTo(actionKey)
                                )
                            ),
                        )
                    )
                )
            )
        }
    }

    // We don't expect this scenario to occur in practice
    @Test
    fun `check if logger logs when check if wallet is empty throws an unexpected exception`() {
        runTest {
            val expected = Throwable("Wallet is not empty")
            whenever(
                mockGetFromOpenSecureStore(eq(AuthTokenStoreKeys.PERSISTENT_ID_KEY)),
            ).thenReturn(
                null,
            )

            doAnswer {
                throw expected
            }.`when`(walletSdk).isEmpty()

            sut.invoke()

            assertThat(logger, hasSize(1))
            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage("Wallet SDK failed to check if wallet is empty"),
                            hasException(
                                instanceOf(
                                    GetPersistentIdException.WalletEmptyCheckFailed::class.java
                                )
                            ),
                            hasCustomKeys(
                                contains(
                                    equalTo(componentKey),
                                    equalTo(actionKey)
                                )
                            ),
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `check if logger logs when get wallet throws wallet exception`() =
        runTest {
            val expected = WalletSdk.WalletSdkError.WalletEmptyCheckFailed()

            // Given token is null
            whenever(
                mockGetFromOpenSecureStore(eq(AuthTokenStoreKeys.PERSISTENT_ID_KEY)),
            ).thenReturn(
                null,
            )
            doAnswer {
                throw expected
            }.`when`(walletSdk).isEmpty()

            sut.invoke()

            assertThat(logger, hasSize(1))
            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage("Wallet SDK failed to check if wallet is empty"),
                            hasException(
                                instanceOf(
                                    GetPersistentIdException.WalletEmptyCheckFailed::class.java
                                )
                            ),
                            hasCustomKeys(
                                contains(
                                    equalTo(componentKey),
                                    equalTo(actionKey)
                                )
                            ),
                        )
                    )
                )
            )
        }
}
