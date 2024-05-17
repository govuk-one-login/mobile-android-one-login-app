package uk.gov.onelogin.ui.components.appbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class GdsTopAppBarProvider : PreviewParameterProvider<GdsAppBar> {
    @OptIn(ExperimentalMaterial3Api::class)
    override val values: Sequence<GdsAppBar> = sequenceOf(
        GdsAppBar(title = { Text(text = "OneLogin") })
    )
}
