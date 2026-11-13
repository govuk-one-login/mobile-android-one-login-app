package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.patterns.leftalignedscreen.LeftAlignedScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport

@Composable
internal fun MockCriOrchestratorContent() {
    GdsTheme {
        LeftAlignedScreen(
            modifier = Modifier.fillMaxSize(),
            title = { GdsHeading(text = "Mock ID Check (Test Build)") },
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview(showBackground = true)
@Composable
internal fun MockCriOrchestratorPreview() {
    MockCriOrchestratorContent()
}
