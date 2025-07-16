package uk.gov.onelogin.features.error.ui.signin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
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
fun SignInErrorUnrecoverableScreen(
    analyticsViewModel: SignInErrorAnalyticsViewModel = hiltViewModel(),
    viewModel: SignInErrorUnrecoverableViewModel = hiltViewModel()
) {
    GdsTheme {
        val context = LocalActivity.current as Activity
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            viewModel.exitApp(context)
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
