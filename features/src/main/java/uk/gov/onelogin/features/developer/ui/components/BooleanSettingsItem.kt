package uk.gov.onelogin.features.developer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.m3.Switch
import uk.gov.android.ui.theme.m3.defaultColors
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport

/**
 * A settings item that lets the user toggle a boolean value.
 */
@Composable
internal fun BooleanSettingsItem(
    text: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = Surface {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = text,
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle(it) },
            colors = Switch.defaultColors(),
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@PreviewLightDark
@Composable
internal fun BooleanSettingsItemPreview() {
    GdsTheme {
        Column {
            BooleanSettingsItem(
                text = "Enabled setting",
                checked = true,
                onToggle = {},
            )
            BooleanSettingsItem(
                text = "Disabled setting",
                checked = false,
                onToggle = {},
            )
        }
    }
}
