package uk.gov.onelogin.tokens.usecases

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.tokens.Keys

class GetTokenExpiryTest {
    private lateinit var useCase: GetTokenExpiry

    private val mockContext: Context = mock()
    private val mockSharedPreferences: SharedPreferences = mock()

    @BeforeEach
    fun setup() {
        whenever(
            mockContext.getSharedPreferences(
                eq(Keys.TOKEN_SHARED_PREFS),
                eq(Context.MODE_PRIVATE)
            )
        ).thenReturn(mockSharedPreferences)

        useCase = GetTokenExpiryImpl(mockContext)
    }

    @Test
    fun `returns null when value does not exist`() {
        whenever(mockSharedPreferences.getLong(Keys.TOKEN_EXPIRY_KEY, 0)).thenReturn(0)

        val result = useCase()

        assertEquals(null, result)
    }

    @Test
    fun `returns expiry value successfully`() {
        val expectedExpiryValue = 123L
        whenever(mockSharedPreferences.getLong(Keys.TOKEN_EXPIRY_KEY, 0)).thenReturn(
            expectedExpiryValue
        )

        val result = useCase()

        assertEquals(expectedExpiryValue, result)
    }
}
