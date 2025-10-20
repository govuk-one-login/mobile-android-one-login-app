package uk.gov.onelogin.core.tokens

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsRefreshTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry

class IsRefreshTokenExpiredImplTest {
    private val mockGetTokenExpiry: GetTokenExpiry = mock()

    private val isRefreshTokenExpired = IsRefreshTokenExpiredImpl(mockGetTokenExpiry)

    @Test
    fun `token not expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(System.currentTimeMillis() + 100)

        val result = isRefreshTokenExpired()

        assertFalse(result)
    }

    @Test
    fun `token expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(System.currentTimeMillis() - 100)

        val result = isRefreshTokenExpired()

        assertTrue(result)
    }

    @Test
    fun `token expiry is null`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(null)

        val result = isRefreshTokenExpired()

        assertTrue(result)
    }
}
