package uk.gov.onelogin.features.developer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport

/**
 * A settings item that lets the user select from a set of strings.
 */
@Composable
internal fun StringSettingItem(
    title: String,
    selected: String,
    options: List<String>,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) = Surface {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = selected)
    }

    if (showDialog) {
        val onDismissRequest = { showDialog = false }
        Dialog(
            onDismissRequest = onDismissRequest,
            content = {
                StringSettingDialogContent(
                    title = title,
                    options = options,
                    onOptionClick = {
                        onDismissRequest()
                        onSelectionChanged(it)
                    },
                )
            },
        )
    }
}

@Composable
private fun StringSettingDialogContent(
    title: String,
    options: List<String>,
    onOptionClick: (String) -> Unit,
) = Surface {
    Column {
        GdsHeading(
            text = title,
            style = GdsHeadingStyle.Title3,
            textAlign = GdsHeadingAlignment.LeftAligned,
            modifier = Modifier.padding(smallPadding),
        )
        options.forEach { option ->
            Text(
                text = option,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionClick(option) }
                    .padding(smallPadding),
            )
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@PreviewLightDark
@Composable
internal fun StringSettingItemPreview() {
    GdsTheme {
        StringSettingItem(
            title = "Selected option",
            selected = "Value",
            options = listOf("Intermittent", "App check failed", "Generic"),
            onSelectionChanged = {},
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@PreviewLightDark
@Composable
internal fun StringSettingDialogContentPreview() {
    GdsTheme {
        StringSettingDialogContent(
            title = "Options",
            options = listOf("Option 1", "Option 2", "Option 3"),
            onOptionClick = {},
        )
    }
}
