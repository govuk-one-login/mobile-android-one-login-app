package uk.gov.onelogin.optin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.android.onelogin.R
import uk.gov.android.ui.theme.disabled_button
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.ui.buttonElevation
import uk.gov.onelogin.ui.buttonPaddingHorizontal
import uk.gov.onelogin.ui.buttonPaddingVertical

@Composable
internal fun DefaultPrimaryButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        contentPadding = PaddingValues(
            vertical = buttonPaddingVertical,
            horizontal = buttonPaddingHorizontal
        ),
        elevation = ButtonDefaults.buttonElevation(buttonElevation),
        enabled = isEnabled,
        shape = RectangleShape,
        colors = primaryButtonColors(),
        onClick = onClick
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            text = buttonText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun DefaultTextButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        contentPadding = PaddingValues(
            vertical = buttonPaddingVertical,
            horizontal = buttonPaddingHorizontal
        ),
        elevation = ButtonDefaults.buttonElevation(buttonElevation),
        enabled = isEnabled,
        shape = RectangleShape,
        colors = textButtonColors(),
        onClick = onClick
    ) {
        Text(
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.labelMedium,
            text = buttonText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun primaryButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor = disabled_button,
    disabledContentColor = MaterialTheme.colorScheme.onPrimary
)

@Composable // TODO verify colour values
private fun textButtonColors() = ButtonDefaults.textButtonColors(
    containerColor = MaterialTheme.colorScheme.secondary,
    contentColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = disabled_button,
    disabledContentColor = MaterialTheme.colorScheme.onPrimary
)

@PreviewFontScale
@PreviewLightDark
@Composable
internal fun DefaultPrimaryButtonPreview() {
    GdsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            DefaultPrimaryButton(
                modifier = Modifier.padding(bottom = 8.dp),
                isEnabled = true,
                buttonText = stringResource(id = R.string.app_shareAnalyticsButton),
                onClick = {}
            )
            DefaultTextButton(
                buttonText = stringResource(id = R.string.app_doNotShareAnalytics),
                isEnabled = true,
                onClick = {}
            )
        }
    }
}
