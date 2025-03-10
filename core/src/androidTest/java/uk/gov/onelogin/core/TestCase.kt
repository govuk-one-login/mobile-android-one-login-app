package uk.gov.onelogin.core

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule

abstract class TestCase {
    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    protected val context: Context = ApplicationProvider.getApplicationContext()
}
