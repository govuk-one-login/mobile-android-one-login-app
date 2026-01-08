package uk.gov.onelogin.core.tokens

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsRefreshTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry

class IsRefreshTokenExpiredImplTest {
    private val mockGetTokenExpiry: GetTokenExpiry = mock()

    private val isRefreshTokenExpired = IsRefreshTokenExpiredImpl(mockGetTokenExpiry)

    @Test
    fun `token not expired`() = runTest {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .plus(1, ChronoUnit.MINUTES)
                .epochSecond
        )

        val result = isRefreshTokenExpired()

        assertFalse(result)
    }

    @Test
    fun `token expired`() = runTest {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .minus(1, ChronoUnit.MINUTES)
                .epochSecond
        )

        val result = isRefreshTokenExpired()

        assertTrue(result)
    }

    @Test
    fun `token expiry is null`() = runTest {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(null)

        val result = isRefreshTokenExpired()

        assertTrue(result)
    }
}
