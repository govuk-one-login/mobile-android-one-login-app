package uk.gov.onelogin.ui.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.components.buttons.ButtonParameters
import uk.gov.android.ui.components.buttons.ButtonType
import uk.gov.android.ui.components.content.ContentParameters
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.components.images.icon.IconParameters
import uk.gov.android.ui.components.information.InformationParameters
import uk.gov.android.ui.pages.errors.ErrorPage
import uk.gov.android.ui.pages.errors.ErrorPageParameters
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.R

@Composable
fun OfflineErrorScreen(navController: NavHostController) {
    GdsTheme {
        ErrorPage(
            parameters = ErrorPageParameters(
                primaryButtonParameters = ButtonParameters(
                    buttonType = ButtonType.PRIMARY(),
                    onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            OFFLINE_ERROR_TRY_AGAIN_KEY,
                            true
                        )
                        navController.popBackStack()
                    },
                    text = R.string.app_tryAgainButton
                ),
                informationParameters = InformationParameters(
                    contentParameters = ContentParameters(
                        resource = listOf(
                            GdsContentText.GdsContentTextString(
                                subTitle = R.string.app_networkErrorTitle,
                                text = arrayOf(R.string.app_networkErrorBody).toIntArray()
                            )
                        ),
                        headingSize = HeadingSize.H1()
                    ),
                    iconParameters = IconParameters(
                        foreGroundColor = Color.Unspecified,
                        image = uk.gov.android.ui.components.R.drawable.ic_error
                    )
                )
            )
        )
    }
}

val OFFLINE_ERROR_TRY_AGAIN_KEY: String = "OFFLINE_ERROR_TRY_AGAIN_KEY"

@Composable
@Preview
private fun Preview() {
    OfflineErrorScreen(rememberNavController())
}
