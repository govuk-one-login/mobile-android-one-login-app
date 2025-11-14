package uk.gov.onelogin.features.signout.ui

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.R as T
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class SignOutBodyTest : FragmentActivityTestCase() {
    private lateinit var title: SemanticsMatcher
    private lateinit var header: SemanticsMatcher
    private lateinit var subtitle: SemanticsMatcher
    private lateinit var bullet1: SemanticsMatcher
    private lateinit var bullet2: SemanticsMatcher
    private lateinit var bullet3: SemanticsMatcher
    private lateinit var footer: SemanticsMatcher
    private lateinit var primaryButton: SemanticsMatcher
    private lateinit var closeButton: SemanticsMatcher

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        with(context) {
            title = hasText(getString(R.string.app_signOutConfirmationTitle))
            header = hasText(getString(R.string.app_signOutConfirmationBody1), substring = true)
            subtitle =
                hasText(
                    getString(R.string.app_signOutConfirmationSubtitle),
                    substring = true
                )
            bullet1 = hasText(getString(R.string.app_signOutConfirmationBullet1), substring = true)
            bullet2 = hasText(getString(R.string.app_signOutConfirmationBullet2), substring = true)
            bullet3 = hasText(getString(R.string.app_signOutConfirmationBullet3), substring = true)
            footer = hasText(getString(R.string.app_signOutConfirmationBody3), substring = true)
            primaryButton = hasText(getString(R.string.app_signOutAndDeleteAppDataButton))
            closeButton = hasContentDescription(getString(T.string.close_icon_button))
        }
    }

    @Test
    fun verifyUI() {
        // Given the SignOutBody Composable
        composeTestRule.setContent {
            SignOutBody({}, {}, {})
        }
        // Then the UI elements are visible
        with(composeTestRule) {
            onNode(title).assertIsDisplayed()
            onNode(header).assertIsDisplayed()
            onNode(subtitle).assertIsDisplayed()
            onNode(bullet1).assertIsDisplayed()
            onNode(bullet2).assertIsDisplayed()
            onNode(bullet3).assertIsDisplayed()
            onNode(footer).assertIsDisplayed()
            onNode(primaryButton).assertIsDisplayed()
            onNode(closeButton).assertIsDisplayed()
        }
    }

    @Test
    fun onClose() {
        // Given the SignOutBody Composable
        var actual = false
        composeTestRule.setContent {
            SignOutBody(
                onPrimary = {},
                onClose = { actual = true },
                onBack = {}
            )
        }
        // When clicking the `closeButton`
        composeTestRule.onNode(closeButton).performClick()
        // Then onClose() is called and the variable is true
        assertEquals(true, actual)
    }

    @Test
    fun onPrimary() {
        // Given the SignOutBody Composable
        var actual = false
        composeTestRule.setContent {
            SignOutBody(
                onPrimary = { actual = true },
                onClose = {},
                onBack = {}
            )
        }
        // When clicking the `primaryButton`
        composeTestRule.onNode(primaryButton).performClick()
        // Then onBack() is called and the variable is true
        assertEquals(true, actual)
    }

    @Test
    fun onBack() {
        // Given the SignOutBody Composable
        var actual = false
        composeTestRule.setContent {
            SignOutBody(
                onPrimary = {},
                onClose = {},
                onBack = { actual = true }
            )
        }
        // When clicking the `primaryButton`
        Espresso.pressBack()
        // Then onPrimary() is called and the variable is true
        assertEquals(true, actual)
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            SignOutPreview()
        }
    }
}
