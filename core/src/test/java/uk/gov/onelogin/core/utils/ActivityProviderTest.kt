package uk.gov.onelogin.core.utils

import androidx.fragment.app.FragmentActivity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class ActivityProviderTest {
    private lateinit var sut: ActivityProvider
    private val fragmentActivity: FragmentActivity = mock()

    @BeforeEach
    fun setup() {
        sut = ActivityProviderImpl()
    }

    @Test
    fun `test set and get activity provider fragment activity`() {
        // WHEN the activity is not set up yet
        // AND we attempt to access it
        // THEN fragmentActivity returned is null
        assertNull(sut.getCurrentActivity())

        // WHEN we then set the current activity
        sut.setCurrentActivity(fragmentActivity)

        // AND attempt to access it
        sut.getCurrentActivity()

        // THEN the fragmentActivity return the value previously set
        assertEquals(fragmentActivity, sut.getCurrentActivity())
    }

    @Test
    fun `test clear activity provider fragment activity`() {
        // WHEN we set the activity provider fragmentActivity
        sut.setCurrentActivity(fragmentActivity)

        // AND attempt to access it
        // THEN it returns a concrete value
        assertEquals(fragmentActivity, sut.getCurrentActivity())

        // WHEN we then clear it
        sut.clearActivity()

        // AND attempt to access the value
        // THEN it returns null
        assertNull(sut.getCurrentActivity())
    }
}
