package uk.gov.onelogin.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.network.auth.response.TokenResponse

@RunWith(AndroidJUnit4::class)
class HomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialisesHomeScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen(
                navController = rememberNavController(),
                tokens = TokenResponse(
                    access = "access_token",
                    expires = 180,
                    id = "id_token",
                    refresh = "refresh_token",
                    scope = "scope",
                    type = "type"
                )
            )
        }
    }
}
