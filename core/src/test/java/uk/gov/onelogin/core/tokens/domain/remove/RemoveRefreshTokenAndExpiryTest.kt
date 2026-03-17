package uk.gov.onelogin.core.tokens.domain.remove

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
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
    private val mockLogger = SystemLogger()
    private val mockContext: Context = mock()
    private val mockSharedPreferences: SharedPreferences = mock()
    private val mockEditor: SharedPreferences.Editor = mock()

    @BeforeEach
    fun setup() {
        whenever(
            mockContext.getSharedPreferences(
                eq(AuthTokenStoreKeys.TOKEN_SHARED_PREFS),
                eq(Context.MODE_PRIVATE),
            ),
        ).thenReturn(mockSharedPreferences)
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)

        sut =
            RemoveRefreshTokenAndExpiryImpl(
                context = mockContext,
                tokenSecureStore = mockTokenSecureStore,
                logger = mockLogger
            )
    }

    @Test
    fun testRemove() =
        runTest {
            sut.remove()

            verify(mockEditor).remove(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY)
            verify(mockEditor).commit()
            verify(mockTokenSecureStore).delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
        }

    @Test
    fun testRemoveSecureStoreFailure() =
        runTest {
            val message = "Error message"
            val exception = SecureStorageErrorV2(Exception(message))
            whenever(mockTokenSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)).thenThrow(exception)
            sut.remove()

            assertTrue(mockLogger.contains("java.lang.Exception: $message"))
        }
}
