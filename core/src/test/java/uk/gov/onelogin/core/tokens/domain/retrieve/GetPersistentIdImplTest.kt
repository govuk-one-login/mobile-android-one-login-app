package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
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

    private val throwableMessage = "This is a unit test!"

    private val logMessage = "Unit test log message"

    private val logTag = "Example log tag"
    val logThrowable = Throwable(message = throwableMessage)

    val errorKeysTest: ErrorKeys =
        ErrorKeys.IntKey(
            "key",
            1,
        )

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

            logger.error(tag = logTag, message = logMessage, throwable = logThrowable, errorKeysTest)
            verify(logger).error(
                eq(logTag),
                eq(logMessage),
                any(),
                eq(errorKeysTest)
            )
        }
    }
}
