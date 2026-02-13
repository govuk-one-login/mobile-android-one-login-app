package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class GetRefreshTokenExpiryTest {
    private lateinit var useCase: GetTokenExpiry
    private val mockOpenSecureStore: GetFromOpenSecureStore = mock()

    @BeforeEach
    fun setup() {
        useCase = GetRefreshTokenExpiryImpl(mockOpenSecureStore)
    }

    @Test
    fun `returns null when value does not exist`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY to "0"))

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns null when value is not a number`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY to "a"))

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns null when value null`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY))
                .thenReturn(null)

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns null when expiry value does not exist`() =
        runTest {
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf())

            val result = useCase()

            assertEquals(null, result)
        }

    @Test
    fun `returns expiry value successfully`() =
        runTest {
            val expectedExpiryValue = 123L
            whenever(mockOpenSecureStore.invoke(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY))
                .thenReturn(mapOf(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY to "123"))

            val result = useCase()

            assertEquals(expectedExpiryValue, result)
        }
}
