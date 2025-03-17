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
import uk.gov.android.ui.components.R as UiR
import uk.gov.android.ui.componentsv2.button.ButtonType
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.navigation.domain.closeApp
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun ErrorUpdateRequiredScreen(
    viewModel: OutdatedAppErrorViewModel = hiltViewModel(),
    analyticsViewModel: OutdatedAppErrorAnalyticsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    UpdateRequiredBody(
        onPrimary = {
            viewModel.updateApp()
            analyticsViewModel.trackAppUpdate()
        }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = smallPadding)
        ) {
            Image(
                painter = painterResource(UiR.drawable.ic_error),
                contentDescription = stringResource(R.string.app_updateApp_ContentDescription),
                modifier = Modifier.padding(mediumPadding)
            )
            GdsHeading(
                text = stringResource(R.string.app_updateApp_Title),
                modifier = Modifier.padding(bottom = smallPadding),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.app_updateAppBody1),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.app_updateAppBody2),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        GdsButton(
            text = stringResource(R.string.app_updateAppButton),
            buttonType = ButtonType.Primary,
            onClick = onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = buttonText + buttonAccessibilityDesc }
        )
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
