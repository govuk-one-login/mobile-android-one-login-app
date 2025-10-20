package uk.gov.onelogin.core.tokens.domain.save

import android.content.Context
import android.content.SharedPreferences
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiryImpl
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.SystemTimeProvider

class SaveTokenExpiryTest {
    private val validRefreshToken = "ewogICJhbGciOiAiRVMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkI" +
        "jogImFiY2QtMTIzNCIKfQ.ewogICJhdWQiOiAidGVzdCIsCiAgImV4cCI6IDE3NjMxMDg2MTcKfQ.abdcd"
    private val invalidRefreshToken = "e30.eyJhdWQiOiJ0ZXN0In0"

    private lateinit var useCase: SaveTokenExpiry

    private val mockContext: Context = mock()
    private val mockLogger: Logger = mock()
    private val mockSharedPreferences: SharedPreferences = mock()
    private val mockEditor: SharedPreferences.Editor = mock()
    private val mockSystemTimeProvider: SystemTimeProvider = mock()

    @BeforeEach
    fun setup() {
        whenever(
            mockContext.getSharedPreferences(
                eq(AuthTokenStoreKeys.TOKEN_SHARED_PREFS),
                eq(Context.MODE_PRIVATE)
            )
        )
            .thenReturn(mockSharedPreferences)
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)

        useCase = SaveTokenExpiryImpl(mockContext, mockLogger, mockSystemTimeProvider)
    }

    @Test
    fun `check expiry saved`() {
        val expiry = 1L
        useCase.saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = expiry
            ),
            ExpiryInfo(
                key = REFRESH_TOKEN_EXPIRY_KEY,
                value = expiry
            )
        )

        verify(mockEditor).putLong(ACCESS_TOKEN_EXPIRY_KEY, expiry)
        verify(mockEditor).putLong(REFRESH_TOKEN_EXPIRY_KEY, expiry)
        verify(mockEditor, times(2)).apply()
    }

    @Test
    fun `check extract exp from refresh successful`() {
        val result = useCase.extractExpFromRefreshToken(validRefreshToken)

        assertEquals(1763108617, result)
    }

    @Test
    fun `check extract exp from refresh unsuccessful and create exp manually`() {
        val mockSec: Long = Instant.ofEpochSecond(1763108617)
            .plus(30, ChronoUnit.DAYS)
            .epochSecond
        whenever(mockSystemTimeProvider.thirtyDaysFromNowTimestampInSeconds()).thenReturn(mockSec)
        val expected: Long = mockSec
        val result = useCase.extractExpFromRefreshToken(invalidRefreshToken)

        assertEquals(expected, result)
    }
}
