package uk.gov.onelogin.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.pages.TitledPage
import uk.gov.android.ui.pages.TitledPageParameters
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.ui.LocaleUtils.getLocale
import uk.gov.onelogin.ui.components.EmailHeader
import uk.gov.onelogin.ui.components.LightRed

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val lang = remember { getLocale(context) }
    TitledPage(
        parameters = TitledPageParameters(
            R.string.app_profile
        ) {
            EmailHeader("someEmail@mail.com")
            HeadingRow(R.string.app_profileSubtitle1)
            ExternalLinkRow(
                R.string.app_signInDetails,
                R.drawable.external_link_icon,
                description = stringResource(
                    id = R.string.app_manageSignInDetailsFootnote
                )
            ) {
                uriHandler.openUri("https://signin.account.gov.uk/sign-in-or-create?lng=$lang")
            }
            HeadingRow(R.string.app_profileSubtitle2)
            ExternalLinkRow(R.string.app_privacyNoticeLink2, R.drawable.external_link_icon) {
                uriHandler.openUri("https://signin.account.gov.uk/privacy-notice?lng=$lang")
            }
            HorizontalDivider()
            ExternalLinkRow(R.string.app_OpenSourceLicences, R.drawable.arrow_right_icon)
            HeadingRow(R.string.app_profileSubtitle3)
            ExternalLinkRow(
                R.string.app_reportAProblemGiveFeedbackLink,
                R.drawable.external_link_icon
            )
            HorizontalDivider()
            ExternalLinkRow(R.string.app_appGuidanceLink, R.drawable.external_link_icon)
            Spacer(modifier = Modifier.height(mediumPadding))
            SignOutRow()
        }
    )
}

@Composable
private fun HeadingRow(@androidx.annotation.StringRes text: Int) {
    GdsHeading(
        HeadingParameters(
            text = text,
            size = HeadingSize.H3(),
            textAlign = TextAlign.Center,
            backgroundColor = MaterialTheme.colorScheme.background,
            padding = PaddingValues(all = smallPadding)
        )
    )
}

@Composable
private fun ExternalLinkRow(
    @androidx.annotation.StringRes title: Int,
    @DrawableRes icon: Int,
    description: String? = null,
    onClick: () -> Unit = { }
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                modifier = Modifier
                    .padding(horizontal = smallPadding)
                    .padding(top = smallPadding)
                    .padding(bottom = if (description == null) smallPadding else 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(title)
            )
            description?.let {
                Text(
                    modifier = Modifier
                        .padding(
                            start = smallPadding,
                            bottom = smallPadding,
                            end = 64.dp
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    text = it
                )
            }
        }
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .padding(end = smallPadding, top = smallPadding)
                .size(24.dp)
                .align(alignment = Alignment.TopEnd)
        )
    }
}

@Composable
private fun SignOutRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .clickable {
            }
    ) {
        Text(
            modifier = Modifier.padding(all = smallPadding).height(24.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.app_signOutButton),
            color = LightRed
        )
    }
}

@Composable
@Preview
private fun Preview() {
    ProfileScreen()
}
