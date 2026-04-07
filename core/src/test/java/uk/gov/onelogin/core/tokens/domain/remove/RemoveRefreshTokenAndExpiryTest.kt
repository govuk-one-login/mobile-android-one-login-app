package uk.gov.onelogin.core.tokens.domain.remove

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import kotlin.test.assertTrue

class RemoveRefreshTokenAndExpiryTest {
    private lateinit var sut: RemoveRefreshTokenAndExpiryImpl
    private val mockTokenSecureStore: SecureStoreAsyncV2 = mock()
    private val mockOpenSecureStore: SecureStoreAsyncV2 = mock()
    private val mockLogger = SystemLogger()

    @BeforeEach
    fun setup() {
        sut =
            RemoveRefreshTokenAndExpiryImpl(
                openSecureStore = mockOpenSecureStore,
                tokenSecureStore = mockTokenSecureStore,
                logger = mockLogger
            )
    }

    @Test
    fun testRemove() =
        runTest {
            sut.remove()

            verify(mockOpenSecureStore).delete(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY)
            verify(mockTokenSecureStore).delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
        }

    @Test
    fun testRemoveTokenSecureStoreFailure() =
        runTest {
            val message = "Error message"
            val exception = SecureStorageErrorV2(Exception(message))
            whenever(mockTokenSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)).thenThrow(exception)
            sut.remove()

            assertTrue(mockLogger.contains("java.lang.Exception: $message"))
        }

    @Test
    fun testRemoveOpenSecureStoreFailure() =
        runTest {
            val message = "Error message"
            val exception = SecureStorageErrorV2(Exception(message))
            whenever(mockOpenSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY)).thenThrow(exception)
            sut.remove()

            assertTrue(mockLogger.contains("java.lang.Exception: $message"))
        }
}
