package uk.gov.onelogin.tokens.usecases

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.tokens.Keys

class RemoveTokenExpiryTest {

    private lateinit var useCase: RemoveTokenExpiry

    private val mockContext: Context = mock()
    private val mockSharedPreferences: SharedPreferences = mock()
    private val mockEditor: SharedPreferences.Editor = mock()

    @BeforeEach
    fun setup() {
        whenever(
            mockContext.getSharedPreferences(
                eq(Keys.TOKEN_SHARED_PREFS),
                eq(Context.MODE_PRIVATE)
            )
        )
            .thenReturn(mockSharedPreferences)
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)

        useCase = RemoveTokenExpiryImpl(mockContext)
    }

    @Test
    fun `check expiry token removed`() {
        useCase()

        verify(mockEditor).remove(Keys.TOKEN_EXPIRY_KEY)
        verify(mockEditor).apply()
    }
}
