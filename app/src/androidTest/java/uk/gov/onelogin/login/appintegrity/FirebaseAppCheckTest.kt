package uk.gov.onelogin.login.appintegrity

import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger
import uk.gov.onelogin.utils.TestCase

@HiltAndroidTest
class FirebaseAppCheckTest : TestCase() {
    private val factory = DebugAppCheckProviderFactory.getInstance()
    private val logger: Logger = mock()
    private lateinit var sut: AppChecker

    @Before
    fun setup() {
        sut = FirebaseAppCheck(factory, context, logger)
    }

    @Test
    fun test_initialise() {
        assertNotNull(Firebase.appCheck)
    }
}
