package uk.gov.onelogin.core

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleInstrumentedTest {
    @Test
    fun test() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("uk.gov.android.onelogin.core.test", appContext.packageName)
    }
}
