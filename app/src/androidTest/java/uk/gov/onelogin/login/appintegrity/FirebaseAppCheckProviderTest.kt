package uk.gov.onelogin.login.appintegrity

import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.utils.TestCase

@HiltAndroidTest
class FirebaseAppCheckProviderTest : TestCase() {
    private val factory = DebugAppCheckProviderFactory.getInstance()
    private lateinit var sut: FirebaseAppCheckProvider

    @Before
    fun setup() {
        sut = FirebaseAppCheckProviderImpl(factory, context)
    }

    @Test
    fun test_initialise() {
        assertNotNull(Firebase.appCheck)
    }
}
