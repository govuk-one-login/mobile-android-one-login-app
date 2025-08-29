package uk.gov.onelogin.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.m3.GdsLocalColorScheme
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun EmailSection(email: String) {
    Row(
        modifier = Modifier
            .background(color = GdsLocalColorScheme.current.listBackground)
            .fillMaxWidth()
            .semantics(true) {
                traversalIndex = EMAIL_TRAVERSAL_ORDER
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            modifier = Modifier.padding(smallPadding)
                .testTag(IMAGE_TEST_TAG),
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = null
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            text = buildAnnotatedString {
                append(LocalContext.current.getText(R.string.app_settingsSignInDetailsTile))
                appendLine()
                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    append(email)
                }
            }
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun EmailSectionPreview() {
    GdsTheme {
        EmailSection("mock@email.com")
    }
}

const val DIVIDER_TEST_TAG = "divider_test_tag"
internal const val IMAGE_TEST_TAG = "image_test_tag"
private const val EMAIL_TRAVERSAL_ORDER = -25f
