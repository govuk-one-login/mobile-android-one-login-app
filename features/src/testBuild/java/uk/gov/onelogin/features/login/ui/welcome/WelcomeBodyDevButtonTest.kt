package uk.gov.onelogin.features.login.ui.welcome

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeBody

@RunWith(AndroidJUnit4::class)
class WelcomeBodyDevButtonTest : FragmentActivityTestCase() {
    private val devButton = hasText(resources.getString(R.string.app_developer_button))

    @Test
    fun onAccessDevMenu() {
        // Given the WelcomeBody Composable
        var actual = false
        composeTestRule.setContent {
            WelcomeBody(
                onSignIn = {},
                openDevMenu = { actual = true }
            )
        }

        // When clicking the icon
        composeTestRule.onAllNodes(devButton, useUnmergedTree = true)[0].performClick()
        // Then openDevMenu() is called and the variable is true
        assertEquals(true, actual)
    }
}
