package uk.gov.onelogin.features.error.ui.update

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.rememberNavController
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
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
fun ErrorUpdateRequiredScreen(
    viewModel: OutdatedAppErrorViewModel = hiltViewModel(),
    analyticsViewModel: OutdatedAppErrorAnalyticsViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    UpdateRequiredBody(
        onPrimary = {
            viewModel.updateApp()
            analyticsViewModel.trackAppUpdate()
        },
    )
    BackHandler {
        analyticsViewModel.trackBackButton()
        navController.closeApp()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { analyticsViewModel.trackUpdateRequiredView() }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
internal fun UpdateRequiredBody(onPrimary: () -> Unit = {}) {
    val buttonText = stringResource(R.string.app_updateAppButton)
    val buttonAccessibilityDesc = stringResource(R.string.app_openGooglePlayStore)
    GdsTheme {
        EdgeToEdgePage { _ ->
            ErrorScreen(
                icon = { padding ->
                    GdsIcon(
                        image = ImageVector.vectorResource(patternsR.drawable.ic_warning_error),
                        contentDescription = stringResource(patternsR.string.error_icon_description),
                        modifier = Modifier.errorBodyItemModifier(padding),
                    )
                },
                title = { padding ->
                    GdsHeading(
                        text = stringResource(R.string.app_updateApp_Title),
                        modifier = Modifier.errorBodyItemModifier(padding),
                        textAlign = GdsHeadingAlignment.CenterAligned,
                    )
                },
                body = { padding ->
                    item {
                        Text(
                            text = stringResource(R.string.app_updateAppBody1),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.errorBodyItemModifier(padding),
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.app_updateAppBody2),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.errorBodyItemModifier(padding),
                        )
                    }
                },
                primaryButton = {
                    GdsButton(
                        text = buttonText,
                        buttonType = ButtonTypeV2.Primary(),
                        onClick = onPrimary,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .semantics(mergeDescendants = true) {
                                    contentDescription = buttonText + buttonAccessibilityDesc
                                },
                    )
                },
            )
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun UpdateRequiredPreview() {
    GdsTheme {
        UpdateRequiredBody {}
    }
}
