package uk.gov.onelogin.features.login.ui.signin.splash

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.developer.DeveloperTools
import uk.gov.onelogin.features.optin.ui.OptInRequirementViewModel

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = hiltViewModel(),
    analyticsViewModel: SplashScreenAnalyticsViewModel = hiltViewModel(),
    optInRequirementViewModel: OptInRequirementViewModel = hiltViewModel()
) {
    val context = LocalActivity.current as FragmentActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val loading = viewModel.loading.collectAsState()
    val unlock = viewModel.showUnlock.collectAsState()

    BackHandler {
        analyticsViewModel.trackBackButton(context, unlock.value)
        context.finishAndRemoveTask()
    }
    SideEffect { analyticsViewModel.trackSplashScreen(context, unlock.value) }

    SplashBody(
        isUnlock = unlock.value,
        loading = loading.value,
        trackUnlockButton = { analyticsViewModel.trackUnlockButton() },
        onLogin = { viewModel.login(context) },
        onOpenDeveloperPortal = { viewModel.navigateToDevPanel() }
    )

    DisposableEffect(key1 = lifecycleOwner) {
        with(lifecycleOwner) {
            lifecycle.addObserver(viewModel)
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.retrieveAppInfo {
                        optInRequirementViewModel.isOptInRequired.collectLatest { isRequired ->
                            handleOptInRequired(isRequired, viewModel, context)
                        }
                    }
                }
            }
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(viewModel)
            }
        }
    }
}

private fun handleOptInRequired(
    isRequired: Boolean,
    viewModel: SplashScreenViewModel,
    context: FragmentActivity
) {
    when {
        isRequired -> viewModel.navigateToAnalyticsOptIn()
        else ->
            if (!viewModel.showUnlock.value) {
                viewModel.login(context)
            }
    }
}

@Composable
internal fun SplashBody(
    isUnlock: Boolean,
    loading: Boolean,
    trackUnlockButton: () -> Unit,
    onLogin: () -> Unit,
    onOpenDeveloperPortal: () -> Unit
) {
    val displayUnlock = isUnlock && !loading
    SubcomposeLayout(
        modifier = Modifier.background(colorResource(R.color.govuk_blue))
    ) { constraints ->
        // Get full specs of device
        val fullHeight = constraints.maxHeight
        val fullWidth = constraints.maxWidth
        // Measure logo and create slot to be displayed
        val logoPlaceable = createSubcomposeLogo(onOpenDeveloperPortal, constraints)
        // Measure crown icon and create slot to be displayed
        val crownPlaceable = createSubcomposeCrown()
        // Measure loading spinner and/ or unlock text and create slot to be displayed
        val dynamicContentPlaceable = subcompose("content") {
            if (displayUnlock) UnlockButton(trackUnlockButton, onLogin) else LoadingIndicator()
        }.first().measure(Constraints(maxWidth = fullWidth))
        // Measure unlock button height to be used to enable equal space distribution between the logo, crown and loading spinner (when this is displayed)
        val loadingSpinnerHeight = subcompose("loading spinner") {
            LoadingIndicator()
        }.first().measure(Constraints(maxWidth = fullWidth)).height

        // Calculate logo height
        val imageHeight = logoPlaceable.maxOf { it.height }
        // Y coordinate for logo to be placed (pos: absolute center at all times)
        val imageY = (fullHeight - imageHeight) / 2
        // Calculate the total space available to display the crown and optional/ dynamic content (loading spinner/ unlock button)
        val availableContentHeight = fullHeight - (imageY + imageHeight) - BOTTOM_PADDING.toPx()
        // Calculate the exact total space taken by the items - assists in defining the gap between items and space distribution to meet design requirements
        val concreteContentHeight = crownPlaceable.height + loadingSpinnerHeight
        // Calculate the max gap that can be added between item to be equally distributed
        val gapHeight =
            ((availableContentHeight - concreteContentHeight).coerceAtLeast(0f)) / 2
        // Y coordinate for crown icon to be centred and have a exact gap between the logo and itself which is equal to the gap between the crown and loading spinner when that is displayed
        val crownY = imageY + imageHeight + gapHeight
        // Y coordinate for loading spinner - includes bottom padding of 48.dp and an equal gap between crown icon as the crown icon to logo
        val loadingY = crownY + crownPlaceable.height + gapHeight
        // Y coordinate for unlock button - this ensured the unlock button is positioned at a 48.dp gap from the screen bottom
        val unlockY =
            crownY + crownPlaceable.height + (concreteContentHeight - BOTTOM_PADDING.toPx())

        // Create layout and place content accordingly
        layout(fullWidth, fullHeight) {
            logoPlaceable.forEach {
                it.placeRelative(x = (fullWidth - it.width) / 2, y = imageY)
            }
            crownPlaceable.placeRelative(
                x = (fullWidth - crownPlaceable.width) / 2,
                y = crownY.roundToInt()
            )
            if (displayUnlock || loading) {
                dynamicContentPlaceable.placeRelative(
                    x = (fullWidth - dynamicContentPlaceable.width) / 2,
                    y = if (loading) loadingY.roundToInt() else unlockY.roundToInt()
                )
            }
        }
    }
}

private fun SubcomposeMeasureScope.createSubcomposeLogo(
    onOpenDeveloperPortal: () -> Unit,
    constraints: Constraints
): List<Placeable> {
    val imagePlaceable = subcompose("logo") {
        Icon(
            painter = painterResource(R.drawable.ic_splash_logo),
            contentDescription = if (DeveloperTools.isDeveloperPanelEnabled()) {
                "Open dev menu"
            } else {
                null
            },
            tint = Color.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(stringResource(id = R.string.splashLogoTestTag))
                .then(
                    // This allows for the logo not to be treated as a button in production/ integration and be ignored for accessibility purposes
                    if (DeveloperTools.isDeveloperPanelEnabled()) {
                        Modifier.clickable(enabled = DeveloperTools.isDeveloperPanelEnabled()) {
                            onOpenDeveloperPortal()
                        }
                    } else {
                        Modifier
                    }
                )
        )
    }.map { it.measure(constraints = constraints) }
    return imagePlaceable
}

private fun SubcomposeMeasureScope.createSubcomposeCrown() =
    subcompose("crown") {
        Icon(
            painter = painterResource(R.drawable.ic_tudor_crown),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.testTag(stringResource(id = R.string.splashCrownIconTestTag))
        )
    }.first().measure(Constraints())

@Composable
private fun UnlockButton(trackUnlockButton: () -> Unit, onLogin: () -> Unit) {
    Button(
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = smallPadding)
            .semantics(true) {}
            .testTag(stringResource(R.string.splashUnlockBtnTestTag)),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            trackUnlockButton()
            onLogin()
        }
    ) {
        Text(
            text = stringResource(R.string.app_unlockButton),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
            color = Color.White
        )
    }
}

@Composable
internal fun LoadingIndicator() {
    val loadingText = stringResource(R.string.app_splashScreenLoadingIndicatorText)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = smallPadding,
                end = smallPadding,
                bottom = PROGRESS_BAR
            )
            .focusGroup()
            .semantics(true) { contentDescription = loadingText }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = smallPadding)
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.govuk_blue),
                trackColor = MaterialTheme.colorScheme.onPrimary,
                strokeCap = StrokeCap.Square,
                modifier = Modifier
                    .width(PROGRESS_BAR)
                    .height(PROGRESS_BAR)
                    .semantics { contentDescription = "" }
                    .testTag(stringResource(R.string.splashLoadingSpinnerTestTag))
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = loadingText,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.semantics { contentDescription = "" }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SplashScreenPreview() {
    GdsTheme {
        SplashBody(
            isUnlock = false,
            loading = false,
            trackUnlockButton = {},
            onLogin = {},
            onOpenDeveloperPortal = {}
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
internal fun UnlockScreenPreview() {
    GdsTheme {
        SplashBody(
            isUnlock = true,
            loading = false,
            trackUnlockButton = {},
            onLogin = {},
            onOpenDeveloperPortal = {}
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
internal fun LoadingSplashScreenPreview() {
    GdsTheme {
        SplashBody(
            isUnlock = false,
            loading = true,
            trackUnlockButton = {},
            onLogin = {},
            onOpenDeveloperPortal = {}
        )
    }
}

val PROGRESS_BAR = 48.dp
val BOTTOM_PADDING = 48.dp
