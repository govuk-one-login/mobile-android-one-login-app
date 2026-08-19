package uk.gov.onelogin.features.home.idcheck.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.gov.android.onelogin.features.R
import uk.gov.android.ui.componentsv2.GdsCard
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ComponentPreview

@Composable
fun HowToProveYourIdentityCard(
    onClick: () -> Unit,
) =
    GdsCard(
        title = stringResource(R.string.app_howToTileTitle),
        body = stringResource(R.string.app_howToTileBody),
        displayPrimary = false,
        shadow = 0.dp,
        onClick = onClick,
        displaySecondary = true,
        buttonText = stringResource(R.string.app_howToTileLink),
        secondaryIcon = null,
        modifier = Modifier.testTag(stringResource(R.string.proveIdentityCardTestTag)),
    )

@ComponentPreview
@Composable
fun HowToProveYourIdentityCardPreview() = GdsTheme {
    HowToProveYourIdentityCard(
        onClick = {}
    )
}
