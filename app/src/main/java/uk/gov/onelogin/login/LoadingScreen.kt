package uk.gov.onelogin.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.android.ui.theme.m3.GdsTheme


@Composable
@Preview
fun LoadingScreen() {
    GdsTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .testTag(LOADING_SCREEN_BOX)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center)
                    .testTag(LOADING_SCREEN_PROGRESS_INDICATOR),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

const val LOADING_SCREEN_BOX = "loadingScreen_box"
const val LOADING_SCREEN_PROGRESS_INDICATOR = "loadingScreen_progressIndicator"
