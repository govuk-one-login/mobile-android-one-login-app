package uk.gov.onelogin.features.settings.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.android.ui.theme.xsmallPadding
import uk.gov.onelogin.core.ui.components.EmailSection
import uk.gov.onelogin.core.ui.pages.TitledPage
import uk.gov.onelogin.features.optin.ui.PrivacyNotice

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    analyticsViewModel: SettingsAnalyticsViewModel = hiltViewModel()
) {
    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) {
        viewModel.checkDeviceBiometricsStatus()
        analyticsViewModel.trackSettingsView()
    }
    val uriHandler = LocalUriHandler.current
    val email = viewModel.email
    val optInState by viewModel.optInState.collectAsStateWithLifecycle(false)
    val settingsScreenLinks = SettingsScreenLinks(
        signInUrl = stringResource(R.string.app_manageSignInDetailsUrl),
        privacyNoticeUrl = stringResource(R.string.privacy_notice_url),
        accessibilityStatementUrl = stringResource(R.string.app_accessibilityStatementUrl),
        helpUrl = stringResource(R.string.app_helpUrl),
        contactUrl = stringResource(R.string.app_contactUrl)
    )
    TitledPage(title = R.string.app_settings) { paddingValues ->
        SettingsScreenBody(
            paddingValues = paddingValues,
            email = email,
            optInState = optInState,
            viewModel = viewModel,
            analyticsViewModel = analyticsViewModel,
            uriHandler = uriHandler,
            settingsScreenLinks = settingsScreenLinks
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun SettingsScreenBody(
    paddingValues: PaddingValues,
    email: String,
    optInState: Boolean,
    viewModel: SettingsScreenViewModel,
    analyticsViewModel: SettingsAnalyticsViewModel,
    uriHandler: UriHandler,
    settingsScreenLinks: SettingsScreenLinks
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(top = smallPadding, bottom = xsmallPadding)
    ) {
        EmailSection(email)
        YourDetailsSection(
            onClick = {
                analyticsViewModel.trackSignInDetailLink()
                uriHandler.openUri(settingsScreenLinks.signInUrl)
            }
        )
        HelpAndFeedbackSection(
            onHelpClick = {
                analyticsViewModel.trackUsingOneLoginLink()
                uriHandler.openUri(settingsScreenLinks.helpUrl)
            },
            onContactClick = {
                analyticsViewModel.trackContactOneLoginLink()
                uriHandler.openUri(settingsScreenLinks.contactUrl)
            }
        )
        AboutTheAppSection(
            optInState = optInState,
            biometricsOptionFeatureFlagEnabled = viewModel.displayBiometricsToggle,
            showBiometricsOption = viewModel.biometricsOptionState.collectAsState().value,
            onBiometrics = {
                analyticsViewModel.trackBiometricsButton()
                viewModel.goToBiometricsOptIn()
            },
            onToggle = {
                viewModel.toggleOptInPreference()
            },
            onPrivacyNoticeClick = {
                analyticsViewModel.trackPrivacyNoticeLink()
                uriHandler.openUri(settingsScreenLinks.privacyNoticeUrl)
            }
        )
        LegalSection(
            onPrivacyNoticeClick = {
                analyticsViewModel.trackPrivacyNoticeLink()
                uriHandler.openUri(settingsScreenLinks.privacyNoticeUrl)
            },
            onAccessibilityStatementClick = {
                analyticsViewModel.trackAccessibilityStatementLink()
                uriHandler.openUri(settingsScreenLinks.accessibilityStatementUrl)
            },
            onOpenSourceLicensesClick = {
                analyticsViewModel.trackOpenSourceButton()
                viewModel.goToOssl()
            }
        )
        SignOutRow(
            openSignOutScreen = {
                analyticsViewModel.trackSignOutButton()
                viewModel.goToSignOut()
            }
        )
    }
}

private data class SettingsScreenLinks(
    val signInUrl: String,
    val privacyNoticeUrl: String,
    val accessibilityStatementUrl: String,
    val helpUrl: String,
    val contactUrl: String
)

@Composable
private fun YourDetailsSection(
    onClick: () -> Unit
) {
    HorizontalDivider()
    LinkRow(
        R.string.app_settingsSignInDetailsLink,
        R.drawable.external_link_icon,
        contentDescription = R.string.app_openLinkExternally,
        description = stringResource(
            id = R.string.app_settingSignInDetailsFootnote
        ),
        traversalIndex = MANAGE_DETAILS_TRAVERSAL_ORDER
    ) {
        onClick()
    }
}

@Composable
private fun LegalSection(
    onPrivacyNoticeClick: () -> Unit,
    onAccessibilityStatementClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit
) {
    LinkRow(
        title = R.string.app_privacyNoticeLink2,
        icon = R.drawable.external_link_icon,
        contentDescription = R.string.app_openLinkExternally,
        traversalIndex = PRIVACY_NOTICE_LINK_TRAVERSAL_ORDER
    ) {
        onPrivacyNoticeClick()
    }
    HorizontalDivider()
    LinkRow(
        title = R.string.app_accessibilityStatement,
        icon = R.drawable.external_link_icon,
        contentDescription = R.string.app_openLinkExternally,
        traversalIndex = ACCESSIBILITY_LINK_TRAVERSAL_ORDER
    ) {
        onAccessibilityStatementClick()
    }
    HorizontalDivider()
    LinkRow(
        title = R.string.app_openSourceLicences,
        icon = R.drawable.arrow_right_icon,
        traversalIndex = OSL_LINK_TRAVERSAL_ORDER
    ) {
        onOpenSourceLicensesClick()
    }
}

@Composable
private fun HelpAndFeedbackSection(
    onHelpClick: () -> Unit,
    onContactClick: () -> Unit
) {
    HeadingRow(R.string.app_settingsSubtitle1, traversalIndex = HELP_TRAVERSAL_ORDER)
    LinkRow(
        title = R.string.app_appGuidanceLink,
        icon = R.drawable.external_link_icon,
        contentDescription = R.string.app_openLinkExternally,
        traversalIndex = USING_LINK_TRAVERSAL_ORDER
    ) {
        onHelpClick()
    }
    HorizontalDivider()
    LinkRow(
        title = R.string.app_contactLink,
        icon = R.drawable.external_link_icon,
        contentDescription = R.string.app_openLinkExternally,
        traversalIndex = CONTACT_LINK_TRAVERSAL_ORDER
    ) {
        onContactClick()
    }
}

@Composable
internal fun AboutTheAppSection(
    optInState: Boolean,
    biometricsOptionFeatureFlagEnabled: Boolean,
    showBiometricsOption: Boolean,
    onBiometrics: () -> Unit,
    onToggle: () -> Unit,
    onPrivacyNoticeClick: () -> Unit
) {
    HeadingRow(text = R.string.app_settingsSubtitle2, traversalIndex = ABOUT_TRAVERSAL_ORDER)
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) { }
    ) {
        if (biometricsOptionFeatureFlagEnabled) {
            if (showBiometricsOption) {
                LinkRow(
                    title = R.string.app_settingsBiometricsField,
                    icon = R.drawable.arrow_right_icon,
                    onClick = onBiometrics,
                    traversalIndex = BIOMETRICS_OPT_IN_TRAVERSAL_ORDER
                )
            }
        }
        PreferenceToggleRow(
            title = R.string.app_settingsAnalyticsToggle,
            checked = optInState,
            onToggle = { onToggle() }
        )
        PrivacyNotice(
            Modifier
                .padding(smallPadding),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.surface
            ),
            privacyNoticeString = stringResource(
                id = R.string.app_settingsAnalyticsToggleFootnote
            ),
            privacyNoticeLink = stringResource(
                id = R.string.app_settingsAnalyticsToggleFootnoteLink
            )
        ) {
            onPrivacyNoticeClick()
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun HeadingRow(
    @StringRes text: Int,
    traversalIndex: Float
) {
    GdsHeading(
        text = stringResource(text),
        textColour = MaterialTheme.colorScheme.onBackground,
        textFontWeight = FontWeight.Bold,
        textAlign = GdsHeadingAlignment.LeftAligned,
        style = GdsHeadingStyle.Title3,
        modifier = Modifier
            .padding(all = smallPadding)
            .background(color = MaterialTheme.colorScheme.background)
            .semantics { this.traversalIndex = traversalIndex }
    )
}

@Composable
private fun LinkRow(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    description: String? = null,
    contentDescription: Int? = null,
    traversalIndex: Float,
    onClick: () -> Unit = { }
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { this.traversalIndex = traversalIndex }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = smallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = smallPadding)
                        .padding(top = smallPadding)
                        .padding(bottom = if (description == null) smallPadding else 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(title),
                    textAlign = TextAlign.Left
                )
                description?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = smallPadding,
                                end = 64.dp
                            ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface,
                        text = it,
                        textAlign = TextAlign.Left
                    )
                }
            }
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription?.let { stringResource(it) } ?: "",
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
internal fun PreferenceToggleRow(
    @StringRes title: Int,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .semantics {
                traversalIndex = ANALYTICS_TOGGLE_TRAVERSAL_ORDER
                role = Role.Switch
                onClick {
                    onToggle()
                    true
                }
            }
            .focusable()
            .padding(
                start = smallPadding,
                end = smallPadding
            )
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            modifier = Modifier
                .testTag(stringResource(id = R.string.optInSwitchTestTag))
                .clearAndSetSemantics { }
        )
    }
    HorizontalDivider()
}

@Composable
private fun SignOutRow(openSignOutScreen: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = mediumPadding)
            .defaultMinSize(minHeight = 56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .clickable {
                openSignOutScreen()
            }
            .semantics { traversalIndex = SIGN_OUT_TRAVERSAL_ORDER }
    ) {
        Text(
            modifier = Modifier
                .padding(all = smallPadding)
                .defaultMinSize(minHeight = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.app_signOutButton),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private const val MANAGE_DETAILS_TRAVERSAL_ORDER = -24f
private const val HELP_TRAVERSAL_ORDER = -23f
private const val USING_LINK_TRAVERSAL_ORDER = -22f
private const val CONTACT_LINK_TRAVERSAL_ORDER = -21f
private const val ABOUT_TRAVERSAL_ORDER = -20f
private const val BIOMETRICS_OPT_IN_TRAVERSAL_ORDER = -19f
private const val ANALYTICS_TOGGLE_TRAVERSAL_ORDER = -18f
const val PRIVACY_NOTICE_TRAVERSAL_ORDER = -17f
private const val PRIVACY_NOTICE_LINK_TRAVERSAL_ORDER = 8f
private const val ACCESSIBILITY_LINK_TRAVERSAL_ORDER = 9f
private const val OSL_LINK_TRAVERSAL_ORDER = 10f
private const val SIGN_OUT_TRAVERSAL_ORDER = 11f
