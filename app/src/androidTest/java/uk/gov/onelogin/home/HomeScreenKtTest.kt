package uk.gov.onelogin.home

import androidx.compose.ui.test.junit4.createComposeRule
import io.qameta.allure.kotlin.junit4.AllureRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.ext.setupComposeTestRule

@RunWith(AllureRunner::class)
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
