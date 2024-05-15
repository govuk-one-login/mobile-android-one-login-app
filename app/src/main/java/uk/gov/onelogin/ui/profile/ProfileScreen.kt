package uk.gov.onelogin.ui.profile

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.android.onelogin.R
import uk.gov.android.ui.pages.TitledPage
import uk.gov.android.ui.pages.TitledPageParameters

@Composable
fun ProfileScreen() {
    TitledPage(
        parameters = TitledPageParameters(
            R.string.app_profile
        ) {
            HorizontalDivider()
            Text(
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = buildAnnotatedString {
                    append(LocalContext.current.getText(R.string.app_displayEmail))
                    appendLine()
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("someEmail@gmail.com")
                    }
                }
            )
            HorizontalDivider()
        }
    )
}

@Composable
@Preview
private fun Preview() {
    ProfileScreen()
}
