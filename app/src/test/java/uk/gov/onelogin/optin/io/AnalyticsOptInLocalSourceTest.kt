package uk.gov.onelogin.optin.io

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.io.AnalyticsOptInLocalSource.Companion.DEFAULT_ORDINAL
import uk.gov.onelogin.optin.io.AnalyticsOptInLocalSource.Companion.OPT_IN_KEY
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class AnalyticsOptInLocalSourceTest {
    private val dispatcher = StandardTestDispatcher()
    private val preferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor= mock()
    private lateinit var source: OptInLocalSource

    @BeforeTest
    fun setUp() {
        source = AnalyticsOptInLocalSource(preferences, dispatcher)
        Dispatchers.setMain(dispatcher)
        whenever(preferences.edit()).thenReturn(editor)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial value is defaulted to None`() = runTest {
        // Given OptInLocalSource
        // When calling update with None
        whenever(preferences.getInt(OPT_IN_KEY, DEFAULT_ORDINAL))
            .thenReturn(AnalyticsOptInState.None.ordinal)
        // When calling getState
        val actual = source.getState()
        // Then call getInt
        verify(preferences).getInt(OPT_IN_KEY, DEFAULT_ORDINAL)
        assertEquals(expected = AnalyticsOptInState.None, actual)
    }

    @Test
    fun `update with None saves to SharedPreferences`() = runTest {
        // Given OptInLocalSource
        // When calling update with None
        source.update(AnalyticsOptInState.None)
        // Then call the SharedPreferences.putInt with 0
        // AND call the editor's commit
        verify(preferences.edit()).putInt(OPT_IN_KEY, AnalyticsOptInState.None.ordinal)
        verify(editor).commit()
    }

    @Test
    fun `update with No saves to SharedPreferences`() = runTest {
        // Given OptInLocalSource
        // When calling update with No
        source.update(AnalyticsOptInState.No)
        // Then call the SharedPreferences.putInt with 1
        // AND call the editor's commit
        verify(preferences.edit()).putInt(OPT_IN_KEY, AnalyticsOptInState.No.ordinal)
        verify(editor).commit()
    }

    @Test
    fun `update with Yes saves to SharedPreferences`() = runTest {
        // Given OptInLocalSource
        // When calling update with Yes
        source.update(AnalyticsOptInState.Yes)
        // Then call the SharedPreferences.putInt with 2
        // AND call the editor's commit
        verify(preferences.edit()).putInt(OPT_IN_KEY, AnalyticsOptInState.Yes.ordinal)
        verify(editor).commit()
    }
}
