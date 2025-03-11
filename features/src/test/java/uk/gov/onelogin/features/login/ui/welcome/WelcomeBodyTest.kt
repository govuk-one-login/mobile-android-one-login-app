package uk.gov.onelogin.features.login.ui.welcome

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeBody

@RunWith(AndroidJUnit4::class)
class WelcomeBodyTest : FragmentActivityTestCase() {
    private lateinit var title: SemanticsMatcher
    private lateinit var subTitle1: SemanticsMatcher
    private lateinit var subTitle2: SemanticsMatcher
    private lateinit var primaryButton: SemanticsMatcher
    private lateinit var icon: SemanticsMatcher

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val resources: Resources = context.resources

        title = hasText(resources.getString(R.string.app_signInTitle))
        subTitle1 = hasText(resources.getString(R.string.app_signInBody1))
        subTitle2 = hasText(resources.getString(R.string.app_signInBody2))
        primaryButton = hasText(resources.getString(R.string.app_signInButton))
        icon = hasContentDescription(resources.getString(R.string.app_signInIconDescription))
    }

    @Test
    fun verifyComponents() {
        // Given
        composeTestRule.setContent {
            WelcomeBody()
        }
        // Then
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(subTitle1).assertIsDisplayed()
        composeTestRule.onNode(subTitle2).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
        composeTestRule.onNode(icon).assertIsDisplayed()
    }

    @Test
    fun onSignIn() {
        // Given the WelcomeBody Composable
        var actual = false
        composeTestRule.setContent {
            WelcomeBody(
                onSignIn = { actual = true },
                onTopIconClick = {}
            )
        }
        // When clicking the `primaryButton`
        composeTestRule.onNode(primaryButton).performClick()
        // Then onShare() is called and the variable is true
        assertEquals(true, actual)
    }

    @Test
    fun onTopIcon() {
        // Given the WelcomeBody Composable
        var actual = false
        composeTestRule.setContent {
            WelcomeBody(
                onSignIn = {},
                onTopIconClick = { actual = true }
            )
        }

        // When clicking the icon
        composeTestRule.onNode(icon).performClick()
        // Then onTopIcon() is called and the variable is true
        assertEquals(true, actual)
    }
}
