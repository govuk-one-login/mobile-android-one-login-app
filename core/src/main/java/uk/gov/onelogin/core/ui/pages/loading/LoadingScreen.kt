package uk.gov.onelogin.core.ui.pages.loading

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.largePadding
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun LoadingScreen(
    analyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
    backHandler: () -> Unit
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
fun LoadingBody() {
    val color = colorScheme.contentColorFor(colorScheme.background)
    val loadingContentDescription = stringResource(R.string.app_loading_content_desc)
    val focusRequester = remember { FocusRequester() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .testTag(LOADING_SCREEN_BOX)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .padding(bottom = mediumPadding)
                .semantics { hideFromAccessibility() }
                .testTag(LOADING_SCREEN_PROGRESS_INDICATOR),
            color = colorScheme.primary,
            trackColor = colorScheme.secondary
        )
        Text(
            modifier = Modifier
                .padding(top = largePadding)
                .semantics { contentDescription = loadingContentDescription }
                .focusRequester(focusRequester)
                .testTag(LOADING_SCREEN_TEXT),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            text = stringResource(R.string.app_loadingBody)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        focusRequester.requestFocus()
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun LoadingPreview() {
    GdsTheme {
        LoadingBody()
    }
}

internal const val LOADING_SCREEN_BOX = "loadingScreen_box"
internal const val LOADING_SCREEN_TEXT = "loadingScreen_text"
const val LOADING_SCREEN_PROGRESS_INDICATOR = "loadingScreen_progressIndicator"
