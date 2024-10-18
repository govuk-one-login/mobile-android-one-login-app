package uk.gov.onelogin.coverage

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import uk.gov.android.ui.theme.m3.GdsTheme

@Composable
fun EmptyScreen2() {}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
private fun Empty2Preview() {
    GdsTheme {
        EmptyScreen2()
    }
}
