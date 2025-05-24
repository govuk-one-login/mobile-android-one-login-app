package uk.gov.onelogin.features.settings.ui.biometricstoggle

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.componentsv2.images.GdsIcon
import uk.gov.android.ui.componentsv2.list.GdsBulletedList
import uk.gov.android.ui.componentsv2.list.ListItem
import uk.gov.android.ui.componentsv2.list.ListTitle
import uk.gov.android.ui.componentsv2.list.TitleType
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.m3.customDynamicColor
import uk.gov.android.ui.theme.m3.temporary_link_color_dark
import uk.gov.android.ui.theme.m3.temporary_link_color_light
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricsToggleScreen(
    viewModel: BiometricsToggleScreenViewModel = hiltViewModel(),
    analyticsViewModel: BiometricsToggleAnalyticsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    BackHandler(true) {
        analyticsViewModel.trackBackButton()
        viewModel.goBack()
    }
    SideEffect {
        viewModel.checkBiometricsAvailable()
    }
    Scaffold(
        topBar = {
            BiometricsTopAppBar(scrollBehavior) {
                analyticsViewModel.trackIconBackButton()
                viewModel.goBack()
            }
        }
    ) { paddingValues ->
        BiometricsToggleBody(
            biometricsEnabled = viewModel.biometricsEnabled,
            walletAvailable = viewModel.walletEnabled,
            toggleBiometrics = { viewModel.toggleBiometrics() },
            padding = paddingValues,
            trackWalletCopy = { analyticsViewModel.trackWalletCopyView() },
            trackNokWalletCopy = { analyticsViewModel.trackNoWalletCopyView() },
            trackToggle = { analyticsViewModel.trackToggleEvent(it) }
        )
    }
}

@Composable
private fun BiometricsToggleBody(
    biometricsEnabled: StateFlow<Boolean>,
    walletAvailable: Boolean,
    padding: PaddingValues,
    toggleBiometrics: () -> Unit,
    trackWalletCopy: () -> Unit,
    trackNokWalletCopy: () -> Unit,
    trackToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(padding)
    ) {
        BiometricsToggleRow(R.string.app_settingsBiometricsField, biometricsEnabled) {
            toggleBiometrics()
            trackToggle(it)
        }
        if (walletAvailable) {
            trackWalletCopy()
            WalletCopyContent()
        } else {
            trackNokWalletCopy()
            NoWalletCopyContent()
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun WalletCopyContent() {
    val bulletListTitle = stringResource(R.string.app_biometricsToggleBody1Wallet)
    val bullet1 = stringResource(R.string.app_biometricsToggleBullet1)
    val bullet2 = stringResource(R.string.app_biometricsToggleBullet2)
    val body2 = stringResource(R.string.app_biometricsToggleBody2Wallet)
    val subtitle = stringResource(R.string.app_biometricsToggleSubtitle)
    val body3 = stringResource(R.string.app_biometricsToggleBody3Wallet)
    Column(modifier = Modifier.padding(vertical = smallPadding, horizontal = smallPadding)) {
        GdsBulletedList(
            title = ListTitle(
                text = bulletListTitle,
                titleType = TitleType.Text
            ),
            bulletListItems = persistentListOf(
                ListItem(bullet1),
                ListItem(bullet2)
            )
        )
        Text(
            text = body2,
            modifier = Modifier.padding(top = smallPadding)
        )
        GdsHeading(
            text = subtitle,
            style = GdsHeadingStyle.Body,
            textAlign = GdsHeadingAlignment.LeftAligned,
            textFontWeight = FontWeight.W700,
            modifier = Modifier.padding(vertical = smallPadding)
        )
        Text(body3)
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun NoWalletCopyContent() {
    val body1 = stringResource(R.string.app_biometricsToggleBody1)
    val body2 = stringResource(R.string.app_biometricsToggleBody2)
    val subtitle = stringResource(R.string.app_biometricsToggleSubtitle)
    val body3 = stringResource(R.string.app_biometricsToggleBody3)
    Column(modifier = Modifier.padding(vertical = smallPadding, horizontal = smallPadding)) {
        Text(text = body1)
        Text(
            text = body2,
            modifier = Modifier.padding(top = smallPadding)
        )
        GdsHeading(
            text = subtitle,
            style = GdsHeadingStyle.Body,
            textAlign = GdsHeadingAlignment.LeftAligned,
            textFontWeight = FontWeight.W700,
            modifier = Modifier.padding(vertical = smallPadding)
        )
        Text(body3)
    }
}

@Composable
private fun BiometricsToggleRow(
    @StringRes title: Int,
    checked: StateFlow<Boolean>,
    onToggle: (Boolean) -> Unit
) {
    val state = checked.collectAsState().value
    var toggle by remember { mutableStateOf(state) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .toggleable(
                role = Role.Switch,
                value = toggle,
                onValueChange = {
                    toggle = !toggle
                    onToggle(toggle)
                }
            )
            .padding(
                start = smallPadding,
                end = smallPadding
            )
            .testTag(stringResource(id = R.string.optInSwitchTestTag))
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = toggle,
            onCheckedChange = {
                toggle = !toggle
                onToggle(toggle)
            },
            // Required for accessibility to not focus on the toggle first landing on the screen (TalkBack enabled)
            modifier = Modifier.clearAndSetSemantics {}
        )
    }
    HorizontalDivider()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, UnstableDesignSystemAPI::class)
private fun BiometricsTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            GdsHeading(
                text = stringResource(R.string.app_biometricsToggleTitle),
                textAlign = GdsHeadingAlignment.LeftAligned,
                modifier = Modifier.fillMaxWidth(),
                style = GdsHeadingStyle.Title2,
                textFontWeight = FontWeight.W400
            )
        },
        navigationIcon = {
            IconButton(navIconClick) {
                GdsIcon(
                    image = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.app_back_icon),
                    color = customDynamicColor(
                        temporary_link_color_light,
                        temporary_link_color_dark
                    )
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun BiometricsToggleEnabledWalletBodyPreview() {
    GdsTheme {
        Scaffold(
            topBar = {
                BiometricsTopAppBar {}
            }
        ) { paddingValues ->
            BiometricsToggleBody(
                MutableStateFlow(false),
                true,
                paddingValues,
                {},
                {},
                {}
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun BiometricsToggleDisabledNoWalletBodyPreview() {
    GdsTheme {
        Scaffold(
            topBar = {
                BiometricsTopAppBar {}
            }
        ) { paddingValues ->
            BiometricsToggleBody(
                MutableStateFlow(true),
                false,
                paddingValues,
                {},
                {},
                {}
            ) {}
        }
    }
}
