package uk.gov.onelogin.features.error.ui.update

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.navigation.domain.closeApp
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.android.ui.patterns.R as UiR

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
internal fun UpdateRequiredBody(onPrimary: () -> Unit) {
    val buttonText = stringResource(R.string.app_updateAppButton)
    val buttonAccessibilityDesc = stringResource(R.string.app_openGooglePlayStore)
    GdsTheme {
        EdgeToEdgePage { _ ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(mediumPadding),
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier =
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f)
                            .padding(bottom = smallPadding),
                ) {
                    Image(
                        painter = painterResource(UiR.drawable.ic_warning_error),
                        contentDescription =
                            stringResource(
                                R.string.app_updateApp_ContentDescription,
                            ),
                        modifier = Modifier.padding(mediumPadding),
                        colorFilter =
                            ColorFilter.tint(
                                color = MaterialTheme.colorScheme.onBackground,
                            ),
                    )
                    GdsHeading(
                        text = stringResource(R.string.app_updateApp_Title),
                        modifier = Modifier.padding(bottom = smallPadding),
                        textAlign = GdsHeadingAlignment.CenterAligned,
                    )
                    Text(
                        text = stringResource(R.string.app_updateAppBody1),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.app_updateAppBody2),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
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
            }
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
