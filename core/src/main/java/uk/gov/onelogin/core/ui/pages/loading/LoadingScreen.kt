package uk.gov.onelogin.core.ui.pages.loading

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.ui.patterns.loadingscreen.v2.LoadingScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun LoadingScreen(
    analyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
    backHandler: () -> Unit,
) {
    BackHandler(true) {
        analyticsViewModel.trackBackButton()
        backHandler()
    }
    LaunchedEffect(Unit) {
        analyticsViewModel.trackLoadingScreenEvent()
    }

    GdsTheme {
        LoadingBody()
    }
}

@Composable
fun LoadingBody(
    modifier: Modifier = Modifier
) = LoadingScreen(
    modifier = modifier.testTag(LOADING_SCREEN_TEST_TAG)
)

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun LoadingPreview() {
    GdsTheme {
        LoadingBody()
    }
}

const val LOADING_SCREEN_TEST_TAG = "loadingScreen"
