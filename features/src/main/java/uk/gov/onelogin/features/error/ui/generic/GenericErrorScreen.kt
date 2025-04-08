package uk.gov.onelogin.features.error.ui.generic

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun GenericErrorScreen(
    analyticsViewModel: GenericErrorAnalyticsViewModel = hiltViewModel(),
    onClick: () -> Unit = { }
) {
    GdsTheme {
        BackHandler {
            analyticsViewModel.trackBackButton()
            onClick()
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }
        GenericErrorBody {
            analyticsViewModel.trackButton()
            onClick()
        }
    }
}

@Composable
private fun GenericErrorBody(
    primaryOnClick: () -> Unit = {}
) {
    ErrorScreen(
        icon = ErrorScreenIcon.ErrorIcon,
        title = stringResource(R.string.app_genericErrorPage),
        body = persistentListOf(
            CentreAlignedScreenBodyContent.Text(
                bodyText = stringResource(R.string.app_genericErrorPageBody)
            )
        ),
        primaryButton =
        CentreAlignedScreenButton(
            text = stringResource(R.string.app_genericErrorPageButton),
            onClick = primaryOnClick
        )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun GenericErrorPreview() {
    GdsTheme {
        GenericErrorBody()
    }
}
