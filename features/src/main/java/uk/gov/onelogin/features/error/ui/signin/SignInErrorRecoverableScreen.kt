package uk.gov.onelogin.features.error.ui.signin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.images.GdsIcon
import uk.gov.android.ui.patterns.errorscreen.v2.ErrorScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.android.ui.theme.meta.ScreenPreview
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.utils.ModifierExtensions.errorBodyItemModifier

@Composable
@Preview
fun SignInErrorRecoverableScreen(
    analyticsViewModel: SignInErrorAnalyticsViewModel = hiltViewModel(),
    viewModel: SignInErrorRecoverableViewModel = hiltViewModel(),
) {
    GdsTheme {
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            viewModel.onBack()
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackRecoverableScreen() }
        EdgeToEdgePage { _ ->
            SignInErrorBody {
                analyticsViewModel.trackButton()
                viewModel.onClick()
            }
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun SignInErrorBody(onPrimary: () -> Unit) {
    val bodyContent =
        persistentListOf(
            stringResource(R.string.app_signInErrorRecoverableBody),
        )

    ErrorScreen(
        icon = { padding ->
            GdsIcon(
                image = ImageVector.vectorResource(uk.gov.android.ui.patterns.R.drawable.ic_warning_error),
                contentDescription = stringResource(uk.gov.android.ui.patterns.R.string.error_icon_description),
                modifier = Modifier.errorBodyItemModifier(padding)
            )
        },
        title = { padding ->
            GdsHeading(
                text = stringResource(R.string.app_signInErrorTitle),
                textAlign = GdsHeadingAlignment.CenterAligned,
                modifier = Modifier.errorBodyItemModifier(padding)
            )
        },
        body = { padding ->
            items(bodyContent.size) { index ->
                Text(
                    text = bodyContent[index],
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.errorBodyItemModifier(padding)
                )
            }
        },
        primaryButton = {
            val text = stringResource(R.string.app_tryAgainButton)
            GdsButton(
                text = text,
                buttonType = ButtonTypeV2.Primary(),
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        },
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
fun SignInErrorRecoverableScreenPreview() {
    GdsTheme {
        SignInErrorBody {}
    }
}
