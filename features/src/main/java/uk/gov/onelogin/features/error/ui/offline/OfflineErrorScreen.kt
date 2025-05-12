package uk.gov.onelogin.features.error.ui.offline

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
fun OfflineErrorScreen(
    analyticsViewModel: OfflineErrorAnalyticsViewModel = hiltViewModel(),
    goBack: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    GdsTheme {
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            goBack()
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }
        OfflineErrorBody {
            analyticsViewModel.trackButton()
            onRetryClick()
        }
    }
}

@Composable
private fun OfflineErrorBody(onPrimary: () -> Unit = {}) {
    ErrorScreen(
        icon = ErrorScreenIcon.ErrorIcon,
        title = stringResource(R.string.app_networkErrorTitle),
        body =
            persistentListOf(
                CentreAlignedScreenBodyContent.Text(
                    bodyText = stringResource(R.string.app_networkErrorBody1)
                ),
                CentreAlignedScreenBodyContent.Text(
                    bodyText = stringResource(R.string.app_networkErrorBody2)
                )
            ),
        primaryButton =
            CentreAlignedScreenButton(
                text = stringResource(R.string.app_genericErrorPageButton),
                onClick = onPrimary
            )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun OfflineErrorPreview() {
    GdsTheme {
        OfflineErrorBody()
    }
}
