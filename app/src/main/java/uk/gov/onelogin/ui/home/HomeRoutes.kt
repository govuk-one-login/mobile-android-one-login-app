package uk.gov.onelogin.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.MainActivityViewModel

object HomeRoutes {
    const val ROOT: String = "/home"
    const val START: String = "$ROOT/start"
    const val PASSCODE_ERROR: String = "$ROOT/passcode_error"

    fun NavGraphBuilder.homeFlowRoutes() {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                val context = LocalContext.current
                val tokenString = context.getSharedPreferences(
                    MainActivityViewModel.TOKENS_PREFERENCES_FILE,
                    Context.MODE_PRIVATE
                ).getString(MainActivityViewModel.TOKENS_PREFERENCES_KEY, "")

                var tokens: TokenResponse? = null
                try {
                    tokens = TokenResponse.jsonDeserialize(tokenString!!)
                } catch (e: IllegalArgumentException) {
                    Log.e(this.javaClass.simpleName, "Failed to deserialize tokens", e)
                }

                HomeScreen(
                    tokens = tokens
                )
            }
            composable(
                route = PASSCODE_ERROR
            ) {
                PasscodeEnabledErrorScreen()
            }
        }
    }
}
