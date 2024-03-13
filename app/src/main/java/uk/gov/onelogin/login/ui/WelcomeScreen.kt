package uk.gov.onelogin.login.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.R
import uk.gov.onelogin.login.WelcomeScreenViewModel
import uk.gov.onelogin.ui.error.OFFLINE_ERROR_TRY_AGAIN_KEY

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    LandingPage(
        landingPageParameters =
        LandingPageParameters(
            content =
            listOf(
                GdsContentText.GdsContentTextString(
                    text =
                    intArrayOf(
                        R.string.signInSubTitle
                    )
                )
            ),
            onPrimary = {
                viewModel.onPrimary(context, navController)
            },
            primaryButtonText = R.string.signInButton,
            title = R.string.signInTitle,
            topIcon = R.drawable.app_icon
        )
    )
    LaunchedEffect(key1 = Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.apply {
            val tryAgain = get(OFFLINE_ERROR_TRY_AGAIN_KEY) ?: false
            if (tryAgain) {
                remove<Boolean>(OFFLINE_ERROR_TRY_AGAIN_KEY)
                viewModel.onPrimary(context, navController)
            }
        }

    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
    GdsTheme {
        WelcomeScreen(navController = rememberNavController())
    }
}
