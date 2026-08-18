package uk.gov.onelogin.features.home.idcheck.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import uk.gov.android.onelogin.core.R as CoreR
import uk.gov.android.onelogin.features.R
import uk.gov.android.ui.componentsv2.R as UiComponentsR
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.button.GdsButtonDefaults
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.patterns.dialog.FullScreenDialogue
import uk.gov.android.ui.patterns.leftalignedscreen.LeftAlignedScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun HowToProveYourIdentityModal(
    onDismissRequest: () -> Unit,
    onGoToGovUkWebsiteClick: () -> Unit,
) = Surface {
    FullScreenDialogue(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
    ) {
        HowToProveYourIdentityGuidanceContent(
            onGoToGovUkWebsiteClick = onGoToGovUkWebsiteClick
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun HowToProveYourIdentityGuidanceContent(
    onGoToGovUkWebsiteClick: () -> Unit,
) = LeftAlignedScreen(
    title = { horizontalPadding ->
        GdsHeading(
            text = stringResource(R.string.app_proveYourIdentityGuidanceTitle),
            textAlign = GdsHeadingAlignment.LeftAligned,
            modifier = Modifier.padding(horizontal = horizontalPadding),
        )
    },
    body = { horizontalPadding ->
        item {
            Text(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody1),
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }

        item {
            Text(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody2),
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }

        item {
            Text(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody3),
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }

        item {
            GdsButton(
                text = stringResource(R.string.app_proveYourIdentityGuidanceLink),
                onClick = onGoToGovUkWebsiteClick,
                contentPosition = Arrangement.Start,
                textAlign = TextAlign.Start,
                buttonType =
                    ButtonTypeV2.Icon(
                        buttonColors = GdsButtonDefaults.defaultSecondaryColors(),
                        icon = ImageVector.vectorResource(UiComponentsR.drawable.ic_external_site),
                        contentDescription = stringResource(CoreR.string.app_openLinkExternally),
                        isIconTrailing = true,
                    ),
                modifier = Modifier.fillMaxWidth(),
                contentModifier = Modifier.fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
            )
        }

        item {
            GdsHeading(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody4),
                style = GdsHeadingStyle.Body,
                textAlign = GdsHeadingAlignment.LeftAligned,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }

        item {
            Text(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody5),
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }

        item {
            Text(
                text = stringResource(R.string.app_proveYourIdentityGuidanceBody6),
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        }
    }
)

@ScreenPreview
@Composable
fun HowToProveYourIdentityModalPreview() = GdsTheme {
    HowToProveYourIdentityModal(
        onDismissRequest = {},
        onGoToGovUkWebsiteClick = {}
    )
}
