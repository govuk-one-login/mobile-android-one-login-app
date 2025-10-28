package uk.gov.onelogin.core.tokens

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsAccessTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry

class IsAccessTokenExpiredImplTest {
    private val mockGetTokenExpiry: GetTokenExpiry = mock()

    private val isAccessTokenExpired = IsAccessTokenExpiredImpl(mockGetTokenExpiry)

    @Test
    fun `token not expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .plus(1, ChronoUnit.MINUTES)
                .epochSecond
        )

        val result = isAccessTokenExpired()

        assertFalse(result)
    }

    @Test
    fun `token expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .minus(1, ChronoUnit.MINUTES)
                .epochSecond
        )

        val result = isAccessTokenExpired()

        assertTrue(result)
    }

    @Test
    fun `token expiry is null`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(null)

        val result = isAccessTokenExpired()

        assertTrue(result)
    }
}
