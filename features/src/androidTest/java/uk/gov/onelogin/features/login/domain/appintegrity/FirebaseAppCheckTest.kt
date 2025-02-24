package uk.gov.onelogin.features.login.domain.appintegrity

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker

class FirebaseAppCheckTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val factory = DebugAppCheckProviderFactory.getInstance()
    private lateinit var sut: AppChecker

    @Before
    fun setup() {
        Firebase.initialize(context)
        sut = FirebaseAppCheck(factory, context)
    }

    @Ignore("Provisionally - I'll make this work on Monday")
    @Test
    fun test_initialise() {
        assertNotNull(Firebase.appCheck)
    }
}
