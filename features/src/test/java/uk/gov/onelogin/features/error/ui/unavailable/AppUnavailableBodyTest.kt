package uk.gov.onelogin.features.error.ui.unavailable

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class AppUnavailableBodyTest : FragmentActivityTestCase() {
    private lateinit var icon: SemanticsMatcher
    private lateinit var header: SemanticsMatcher
    private lateinit var content: SemanticsMatcher

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        icon = hasTestTag(ICON_TAG)
        header = hasText(context.getString(R.string.app_appUnavailableTitle))
        content = hasText(context.getString(R.string.app_appUnavailableBody))
    }

    @Test
    fun verifyUI() {
        // Given the AppUnavailableBody Composable
        composeTestRule.setContent {
            AppUnavailableBody()
        }
        // Then the UI elements are visible
        composeTestRule.onNode(icon).assertIsDisplayed()
        composeTestRule.onNode(header).assertIsDisplayed()
        composeTestRule.onNode(content).assertIsDisplayed()
    }
}
