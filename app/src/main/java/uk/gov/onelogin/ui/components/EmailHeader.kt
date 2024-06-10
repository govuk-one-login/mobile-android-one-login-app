package uk.gov.onelogin.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import uk.gov.android.onelogin.R
import uk.gov.android.ui.theme.smallPadding

@Composable
fun EmailHeader(email: String) {
    HorizontalDivider()
    Text(
        modifier = Modifier.padding(smallPadding),
        style = MaterialTheme.typography.bodyMedium,
        text = buildAnnotatedString {
            append(LocalContext.current.getText(R.string.app_displayEmail))
            appendLine()
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(email)
            }
        }
    )
    HorizontalDivider()
}
