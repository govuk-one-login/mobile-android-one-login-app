package uk.gov.onelogin.features.error.ui.update

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.rememberNavController
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.components.R as UiR
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.navigation.domain.closeApp

@Composable
fun UpdateRequiredScreen(
    viewModel: UpdateRequiredErrorViewModel = hiltViewModel(),
    analyticsViewModel: UpdateRequiredAnalyticsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    UpdateRequiredBody(
        onPrimary = {
            viewModel.updateApp()
            analyticsViewModel.trackAppUpdate()
        }
    )
    BackHandler {
        analyticsViewModel.trackBackButton()
        navController.closeApp()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { analyticsViewModel.trackUpdateRequiredView() }
}

@Composable
internal fun UpdateRequiredBody(onPrimary: () -> Unit) {
    LandingPage(
        landingPageParameters = LandingPageParameters(
            topIcon = UiR.drawable.ic_error,
            contentDescription = R.string.app_updateApp_ContentDescription,
            iconColor = MaterialTheme.colorScheme.onBackground,
            title = R.string.app_updateApp_Title,
            titleBottomPadding = smallPadding,
            content = listOf(
                GdsContentText.GdsContentTextString(
                    intArrayOf(R.string.app_updateAppBody1)
                ),
                GdsContentText.GdsContentTextString(
                    intArrayOf(R.string.app_updateAppBody2)
                )
            ),
            contentInternalPadding = PaddingValues(bottom = smallPadding),
            primaryButtonText = R.string.app_updateAppButton,
            onPrimary = onPrimary
        )
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
internal fun UpdateRequiredPreview() {
    GdsTheme {
        UpdateRequiredBody {}
    }
}
