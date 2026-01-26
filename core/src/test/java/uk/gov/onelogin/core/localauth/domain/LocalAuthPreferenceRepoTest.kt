package uk.gov.onelogin.core.localauth.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepositoryImpl.Companion.LOCAL_AUTH_PREF
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepositoryImpl.Companion.SHARED_PREFS_ID
import java.util.stream.Stream

class LocalAuthPreferenceRepoTest {
    private val mockContext: FragmentActivity = mock()
    private val mockSharedPreferences: SharedPreferences = mock()
    private val mockEditor: SharedPreferences.Editor = mock()

    private lateinit var bioPrefHandler: LocalAuthPreferenceRepositoryImpl

    @BeforeEach
    fun setUp() {
        whenever(mockContext.getSharedPreferences(eq(SHARED_PREFS_ID), eq(Context.MODE_PRIVATE)))
            .thenReturn(mockSharedPreferences)
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)
        bioPrefHandler = LocalAuthPreferenceRepositoryImpl(mockContext)
    }

    @Test
    fun `check setting bio pref`() {
        bioPrefHandler.setLocalAuthPref(LocalAuthPreference.Enabled(true))

        verify(mockEditor)
            .putString(LOCAL_AUTH_PREF, LocalAuthPreference.Enabled(true).toString())
        verify(mockEditor).apply()
    }

    @ParameterizedTest
    @MethodSource("getBioPrefArgs")
    fun `check getting bio pref`(sharedPrefReturn: LocalAuthPreference?) {
        whenever(mockSharedPreferences.getString(eq(LOCAL_AUTH_PREF), eq(null))).thenReturn(
            sharedPrefReturn?.toString(),
        )
        val bioPref = bioPrefHandler.getLocalAuthPref()

        assertEquals(sharedPrefReturn, bioPref)
    }

    @Test
    fun `check clear preference`() =
        runTest {
            val result = bioPrefHandler.clean()

            verify(mockEditor).clear()
            verify(mockEditor).commit()
            assertEquals(Result.success(Unit), result)
        }

    companion object {
        @JvmStatic
        fun getBioPrefArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(LocalAuthPreference.Enabled(true)),
                Arguments.of(LocalAuthPreference.Enabled(false)),
                Arguments.of(LocalAuthPreference.Disabled),
                Arguments.of(null),
            )
    }
}
