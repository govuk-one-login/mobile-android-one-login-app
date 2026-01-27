package uk.gov.onelogin.features.error.ui.signout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.images.GdsIcon
import uk.gov.android.ui.patterns.errorscreen.ErrorScreen
import uk.gov.android.ui.patterns.errorscreen.v2.ErrorScreen
import uk.gov.android.ui.theme.largePadding
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.android.ui.theme.meta.ScreenPreview
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage

@Composable
fun SignOutErrorScreen(viewModel: SignOutErrorViewModel = hiltViewModel()) {
    SignOutErrorBody { viewModel.goBackToSettingsScreen() }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun SignOutErrorBody(onBackToSettings: () -> Unit) {
    val bodyContent =
        persistentListOf(
            stringResource(R.string.app_signOutErrorBody1),
            stringResource(R.string.app_signOutErrorBody2)
        )
    GdsTheme {
        EdgeToEdgePage { _ ->
            ErrorScreen(
                icon = {
                    GdsIcon(
                        image = ImageVector.vectorResource(uk.gov.android.ui.patterns.R.drawable.ic_warning_error),
                        contentDescription = stringResource(uk.gov.android.ui.componentsv2.R.string.warning),
                        modifier = signOutErrorScreenModifier().padding(bottom = largePadding)
                    )
                },
                title = {
                    GdsHeading(
                        text = stringResource(R.string.app_signOutErrorTitle),
                        modifier = signOutErrorScreenModifier().padding(horizontal = largePadding),
                        textAlign = GdsHeadingAlignment.CenterAligned,
                    )
                },
                body = {
                    items(bodyContent.size) { index ->
                        Text(
                            text = bodyContent[index],
                            textAlign = TextAlign.Center,
                            modifier = signOutErrorScreenModifier().padding(horizontal = largePadding)
                        )
                    }
                },
                primaryButton = {
                    val text = stringResource(R.string.app_SignOutErrorButton)
                    GdsButton(
                        text = text,
                        buttonType = ButtonTypeV2.Primary(),
                        onClick = onBackToSettings,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
            )
            BackHandler {
                onBackToSettings()
            }
        }
    }
}

private fun signOutErrorScreenModifier(): Modifier = Modifier.fillMaxWidth()

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
fun SignOutPreview() {
    SignOutErrorBody {}
}
