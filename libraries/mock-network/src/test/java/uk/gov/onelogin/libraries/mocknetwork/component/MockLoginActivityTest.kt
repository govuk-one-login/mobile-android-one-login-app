package uk.gov.onelogin.libraries.mocknetwork.component

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import uk.gov.onelogin.libraries.mocknetwork.auth.MockLoginActivity

@RunWith(AndroidJUnit4::class)
class MockLoginActivityTest {

    @Test
    fun `finishes immediately with an OK result and non-null data`() {
        val controller = Robolectric.buildActivity(MockLoginActivity::class.java).setup()
        val activity = controller.get()

        assertTrue(activity.isFinishing)

        val shadow = shadowOf(activity)
        assertEquals(Activity.RESULT_OK, shadow.resultCode)
        assertNotNull(shadow.resultIntent)
    }
}
