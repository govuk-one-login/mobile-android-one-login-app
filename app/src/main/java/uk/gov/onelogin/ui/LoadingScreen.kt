package uk.gov.onelogin.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.android.onelogin.R
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.meta.ScreenPreview

@Composable
fun LoadingScreen() {
    GdsTheme {
        LoadingBody()
    }
}

@Composable
fun LoadingBody() {
    val color = colorScheme.contentColorFor(colorScheme.background)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .testTag(LOADING_SCREEN_BOX)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .padding(bottom = smallPadding)
                .testTag(LOADING_SCREEN_PROGRESS_INDICATOR),
            color = colorScheme.primary,
            trackColor = colorScheme.secondary
        )
        Text(
            modifier = Modifier
                .semantics { heading() }
                .testTag(LOADING_SCREEN_TEXT),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            text = stringResource(R.string.app_loadingBody)
        )
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
internal const val LOADING_SCREEN_PROGRESS_INDICATOR = "loadingScreen_progressIndicator"
