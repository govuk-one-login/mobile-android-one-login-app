package uk.gov.onelogin.core.tokens.domain.retrieve

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class GetAccessTokenExpiryTest {
    private lateinit var useCase: GetTokenExpiry

    private val context: Context = mock()
    private val mockOpenSecureStore: GetFromOpenSecureStore = mock()
    private val mockSharedPreferences: SharedPreferences = mock()

    private val validSharedPrefsValue = 172L

    @BeforeEach
    fun setup() {
        whenever(
            context.getSharedPreferences(
                eq(AuthTokenStoreKeys.TOKEN_SHARED_PREFS),
                eq(Context.MODE_PRIVATE)
            )
        ).thenReturn(mockSharedPreferences)

        useCase = GetAccessTokenExpiryImpl(context, mockOpenSecureStore)
    }

    @Test
    fun `returns null when OpenSecureStore value is 0 and sharedPrefs value is 0`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "0"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(0)

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns SharedPrefs when OpenSecureStore value is 0 and sharedPrefs is a valid value`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "0"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(validSharedPrefsValue)

            val result = useCase()

            assertEquals(validSharedPrefsValue, result)
        }

    @Test
    fun `returns sharedPrefs val when OpenSecureStore !=number and sharedPrefs val is valid`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "a"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(validSharedPrefsValue)

            val result = useCase()

            assertEquals(validSharedPrefsValue, result)
        }

    @Test
    fun `returns null when when OpenSecureStore value!=number and sharedPrefs is 0`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "a"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(0)

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns null when when OpenSecureStore value is null and sharedPrefs is a valid value`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(null)
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(validSharedPrefsValue)

            val result = useCase()

            assertEquals(validSharedPrefsValue, result)
        }

    @Test
    fun `returns null when when OpenSecureStore value is null and sharedPrefs is 0`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(null)
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(0)

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns null when OpenSecureStore value is empty and sharedPrefs is a valid value`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf())
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(0)

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns expiry from OpenSecureStore when sharedPrefs value is invalid`() =
        runTest {
            val expectedExpiryValue = 123L
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "123"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(0)

            val result = useCase()

            assertEquals(expectedExpiryValue, result)
        }

    @Test
    fun `returns expiry from OpenSecureStore when sharedPrefs value is valid`() =
        runTest {
            val expectedExpiryValue = 123L
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY to "123"))
            whenever(mockSharedPreferences.getLong(AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY, 0))
                .thenReturn(172)

            val result = useCase()

            assertEquals(expectedExpiryValue, result)
        }
}
