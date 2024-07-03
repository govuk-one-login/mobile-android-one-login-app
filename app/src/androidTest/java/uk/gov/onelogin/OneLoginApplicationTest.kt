package uk.gov.onelogin

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OneLoginApplicationTest {
    @Test
    fun initialiseOneLoginApplication() {
        OneLoginApplication()
    }
}
