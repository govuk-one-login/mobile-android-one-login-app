package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.MockitoHelper
import kotlin.test.assertNull

class GetPersistentIdImplTest {
    private val expectedPersistentId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
    private val mockGetFromOpenSecureStore: GetFromOpenSecureStore = mock()

    private val walletSdk: WalletSdk = mock()

    private val logger: Logger = mock()

    private val logTag = "GetPersistentIdImpl"
    private val logMessage = "Wallet is not empty"

    val errorKeysTest = ErrorKeys.StringKey("reason", "secure wallet data deleted")

    val tag = "GetPersistentIdImpl"
    val logMessageWalletError = "java.lang.Exception: could not determine if wallet is empty"
    val walletErrorErrorKeys = ErrorKeys.StringKey("reason", logMessageWalletError)

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
            verify(logger).error(
                eq(logTag),
                eq(logMessage),
                any(),
                eq(errorKeysTest)
            )
        }
    }

    @Test
    fun `check if logger logs when check if wallet is empty throws an exception`() {
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

            verify(logger).error(
                eq(logTag),
                eq(logMessage),
                any(),
                eq(errorKeysTest)
            )
        }
    }

    @Test
    fun `check if logger logs when get wallet throws exception`() {
        val expected = Error("could not determine if wallet is empty")
        whenever(
            walletSdk.isEmpty()
        ).thenThrow(
            expected
        )
        logger.error(tag = logTag, message = logMessage, throwable = expected, errorKeysTest)
        verify(logger).error(
            eq(logTag),
            eq(logMessage),
            any(),
            eq(errorKeysTest)
        )
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
            verify(logger).error(
                eq(tag),
                eq(logMessageWalletError),
                eq(expected),
                eq(walletErrorErrorKeys)
            )
        }
}
