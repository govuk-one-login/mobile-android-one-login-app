package uk.gov.onelogin.features.error.ui.unavailable

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.rememberNavController
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.images.GdsIcon
import uk.gov.android.ui.patterns.errorscreen.v2.ErrorScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.navigation.domain.closeApp
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.utils.ModifierExtensions.errorBodyItemModifier
import uk.gov.android.ui.patterns.R as patternsR

@Composable
fun AppUnavailableScreen(analyticsViewModel: AppUnavailableAnalyticsViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    GdsTheme {
        AppUnavailableBody()
    }
    BackHandler {
        analyticsViewModel.trackBackButton()
        navController.closeApp()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { analyticsViewModel.trackUnavailableView() }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
internal fun AppUnavailableBody() {
    EdgeToEdgePage { _ ->
        ErrorScreen(
            icon = { horizontalPadding ->
                GdsIcon(
                    image = ImageVector.vectorResource(patternsR.drawable.ic_warning_error),
                    contentDescription = stringResource(patternsR.string.error_icon_description),
                    modifier = Modifier.errorBodyItemModifier(horizontalPadding),
                )
            },
            title = { horizontalPadding ->
                GdsHeading(
                    text = stringResource(R.string.app_appUnavailableTitle),
                    modifier = Modifier.errorBodyItemModifier(horizontalPadding),
                    textAlign = GdsHeadingAlignment.CenterAligned,
                )
            },
            body = { horizontalPadding ->
                item {
                    Text(
                        text = stringResource(R.string.app_appUnavailableBody),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.errorBodyItemModifier(horizontalPadding),
                    )
                }
            },
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun AppUnavailablePreview() {
    GdsTheme { AppUnavailableBody() }
}
