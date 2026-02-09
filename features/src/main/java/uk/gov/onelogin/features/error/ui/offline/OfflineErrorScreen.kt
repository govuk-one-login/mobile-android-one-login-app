package uk.gov.onelogin.features.error.ui.offline

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import uk.gov.android.ui.patterns.errorscreen.v2.ErrorScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.utils.ModifierExtensions.errorBodyItemModifier

@Composable
fun OfflineErrorScreen(
    analyticsViewModel: OfflineErrorAnalyticsViewModel = hiltViewModel(),
    goBack: () -> Unit = {},
) {
    GdsTheme {
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            goBack()
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }
        EdgeToEdgePage { _ ->
            OfflineErrorBody {
                analyticsViewModel.trackButton()
                goBack()
            }
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun OfflineErrorBody(primaryOnClick: () -> Unit = {}) {
    val bodyContent =
        persistentListOf(
            stringResource(R.string.app_networkErrorBody1),
            stringResource(R.string.app_networkErrorBody2)
        )

    ErrorScreen(
        icon = { padding ->
            GdsIcon(
                image = ImageVector.vectorResource(uk.gov.android.ui.patterns.R.drawable.ic_warning_error),
                contentDescription = stringResource(uk.gov.android.ui.patterns.R.string.error_icon_description),
                modifier = errorBodyItemModifier(padding)
            )
        },
        title = { padding ->
            GdsHeading(
                text = stringResource(R.string.app_networkErrorTitle),
                textAlign = GdsHeadingAlignment.CenterAligned,
                modifier = errorBodyItemModifier(padding)
            )
        },
        body = { padding ->
            items(bodyContent.size) { index ->
                Text(
                    text = bodyContent[index],
                    textAlign = TextAlign.Center,
                    modifier = errorBodyItemModifier(padding),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        primaryButton = {
            val text = stringResource(R.string.app_genericErrorPageButton)
            GdsButton(
                text = text,
                buttonType = ButtonTypeV2.Primary(),
                onClick = primaryOnClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = smallPadding)
            )
        },
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun OfflineErrorPreview() {
    GdsTheme {
        OfflineErrorBody()
    }
}
