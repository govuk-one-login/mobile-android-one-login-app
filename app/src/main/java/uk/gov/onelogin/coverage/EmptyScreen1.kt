package uk.gov.onelogin.coverage

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import uk.gov.android.ui.theme.m3.GdsTheme

@Suppress("EmptyFunctionBlock")
@Composable
fun EmptyScreen1() {}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
internal fun Empty1Preview() {
    GdsTheme {
        EmptyScreen1()
    }
}
