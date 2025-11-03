package uk.gov.onelogin.core.tokens

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsAccessTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry

class IsAccessTokenExpiredImplTest {
    private lateinit var mockGetTokenExpiry: GetTokenExpiry

    private lateinit var isAccessTokenExpired: IsTokenExpired

    @BeforeEach
    fun setup() {
        mockGetTokenExpiry = mock()
        isAccessTokenExpired = IsAccessTokenExpiredImpl(mockGetTokenExpiry)
    }

    @Test
    fun `token not expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .plus(1, ChronoUnit.HOURS)
                .toEpochMilli()
        )

        println("expiry: ${mockGetTokenExpiry.invoke()}")
        println("access token: ${isAccessTokenExpired()}")
        val result = isAccessTokenExpired()

        assertFalse(result)
    }

    @Test
    fun `token expired`() {
        whenever(mockGetTokenExpiry.invoke()).thenReturn(
            Instant.now()
                .minus(1, ChronoUnit.MINUTES)
                .toEpochMilli()
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
