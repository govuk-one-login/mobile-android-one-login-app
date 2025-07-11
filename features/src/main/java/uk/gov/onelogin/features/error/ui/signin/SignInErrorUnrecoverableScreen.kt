package uk.gov.onelogin.features.error.ui.signin

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenBodyContent
import uk.gov.android.ui.patterns.errorscreen.ErrorScreen
import uk.gov.android.ui.patterns.errorscreen.ErrorScreenIcon
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.android.ui.theme.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage

@Composable
@Preview
fun SignInErrorUnrecoverableScreen(
    analyticsViewModel: SignInErrorAnalyticsViewModel = hiltViewModel(),
    goBack: () -> Unit = {}
) {
    GdsTheme {
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            goBack()
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackUnrecoverableScreen() }
        EdgeToEdgePage { _ ->
            SignInErrorBody()
        }
    }
}

@Composable
private fun SignInErrorBody() {
    ErrorScreen(
        icon = ErrorScreenIcon.ErrorIcon,
        title = stringResource(R.string.app_signInErrorTitle),
        body = persistentListOf(
            CentreAlignedScreenBodyContent.Text(
                bodyText = stringResource(R.string.app_genericErrorPageBody)
            )
        )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
fun SignInErrorUnrecoverableScreenPreview() {
    GdsTheme {
        SignInErrorBody()
    }
}
