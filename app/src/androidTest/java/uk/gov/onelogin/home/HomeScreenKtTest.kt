package uk.gov.onelogin.home

import androidx.compose.ui.test.junit4.createComposeRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule

@HiltAndroidTest
class HomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialisesHomeScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
    }
}
