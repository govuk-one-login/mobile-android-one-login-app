package uk.gov.onelogin.features.unit.login.domain.validateWalletStoreId

import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.android.onelogin.features.BuildConfig
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasCustomKeys
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasLogEntry
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.retrieve.FakeGetFromOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreIdImpl
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreIdImpl

class ValidateWalletStoreIdTest {
    private val fakeGetFromOpenSecureStore = FakeGetFromOpenSecureStore()
    private val logger = MemorisedLogger()
    private val getWalletStoreId = GetWalletStoreIdImpl(fakeGetFromOpenSecureStore)

    private val walletComponentKey = componentKey("wallet_store_id")
    private val walletActionKey = actionKey("Get wallet store ID")

    private fun sut(persistentId: String? = "persistentId") =
        ValidateWalletStoreIdImpl(
            getWalletStoreId = getWalletStoreId,
            getPersistentId = { persistentId },
            logger = logger
        )

    @Test
    fun `returns true when wallet store id is present`() =
        runTest {
            fakeGetFromOpenSecureStore["wallet_id"] = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"

            val result = sut().invoke()

            assertTrue(result)
        }

    @Test
    fun `returns false and logs error when wallet id is empty`() =
        runTest {
            fakeGetFromOpenSecureStore["wallet_id"] = ""

            val result = sut().invoke()

            assertFalse(result)
            assertThat(logger, hasSize(1))
            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage("Wallet store ID is missing from device storage"),
                            hasException(
                                instanceOf(
                                    ValidateWalletStoreIdImpl.WalletStoreIdMissingException::class.java
                                )
                            ),
                            hasCustomKeys(
                                contains(
                                    equalTo(walletComponentKey),
                                    equalTo(walletActionKey)
                                )
                            )
                        )
                    )
                )
            )
        }

    @Test
    fun `throws AssertionError when persistent id is null`() =
        runTest {
            if (BuildConfig.DEBUG) {
                val exception =
                    assertThrows<AssertionError> {
                        sut(null).invoke()
                    }
                assertEquals("User must be signed-in before validating wallet store ID", exception.message)
            }
        }

    @Test
    fun `returns false and logs error when store returns null`() =
        runTest {
            val result = sut().invoke()

            assertFalse(result)
            assertThat(logger, hasSize(1))
            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage("Wallet store ID is missing from device storage"),
                            hasException(
                                instanceOf(
                                    ValidateWalletStoreIdImpl.WalletStoreIdMissingException::class.java
                                )
                            ),
                            hasCustomKeys(
                                contains(
                                    equalTo(walletComponentKey),
                                    equalTo(walletActionKey)
                                )
                            )
                        )
                    )
                )
            )
        }
}
