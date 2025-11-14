package uk.gov.onelogin.features.error.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenBodyContent
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenButton
import uk.gov.android.ui.patterns.errorscreen.ErrorScreen
import uk.gov.android.ui.patterns.errorscreen.ErrorScreenIcon
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage

@Composable
fun AuthErrorScreen(viewModel: AuthErrorViewModel = hiltViewModel()) {
    GdsTheme {
        EdgeToEdgePage { _ ->
            AuthErrorBody {
                viewModel.navigateToSignIn()
            }
        }
    }
}

@Composable
private fun AuthErrorBody(
    goToSignIn: () -> Unit = {}
) {
    val bodyContent = persistentListOf(
        CentreAlignedScreenBodyContent.Text(
            bodyText = stringResource(R.string.app_dataDeletedBody1)
        ),
        CentreAlignedScreenBodyContent.Text(
            bodyText = stringResource(R.string.app_dataDeletedBody2)
        ),
        CentreAlignedScreenBodyContent.Text(
            bodyText = stringResource(R.string.app_dataDeletedBody3)
        )
    )

    ErrorScreen(
        icon = ErrorScreenIcon.ErrorIcon,
        title = stringResource(R.string.app_dataDeletedErrorTitle),
        body = bodyContent,
        primaryButton =
        CentreAlignedScreenButton(
            text = stringResource(R.string.app_dataDeletedButton),
            onClick = goToSignIn
        )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun AuthErrorScreenPreview() {
    GdsTheme {
        AuthErrorBody()
    }
}
