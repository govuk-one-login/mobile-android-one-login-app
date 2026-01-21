package uk.gov.onelogin

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DeeplinkActivityTest {
    @get:Rule(order = 2)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<DeeplinkActivity>

    @Before
    fun setup() {
        Intents.init()
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        scenario.close()
        Intents.release()
    }

    @Test
    fun verifyNavToMainActivity() = runTest {
        scenario = ActivityScenario.launch(DeeplinkActivity::class.java)
        intended(
            allOf(
                hasComponent(MainActivity::class.java.name)
            )
        )
    }
}
