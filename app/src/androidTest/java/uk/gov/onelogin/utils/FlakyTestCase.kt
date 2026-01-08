package uk.gov.onelogin.utils

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import uk.gov.onelogin.HiltTestActivity

abstract class FlakyTestCase {
    @get:Rule(order = 1)
    var flakyTestRule = FlakyTestRule()

    @get:Rule(order = 2)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 3)
    val composeTestRule = RetryableComposeTestRule { createAndroidComposeRule<HiltTestActivity>() }

    lateinit var navController: TestNavHostController

    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected val resources: Resources = context.resources
}
