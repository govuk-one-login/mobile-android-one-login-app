package uk.gov.onelogin.features.signout.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.list.GdsBulletedList
import uk.gov.android.ui.componentsv2.list.ListItem
import uk.gov.android.ui.componentsv2.list.ListTitle
import uk.gov.android.ui.componentsv2.list.TitleType
import uk.gov.android.ui.patterns.dialog.FullScreenDialogue
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel

@Composable
fun SignOutScreen(
    viewModel: SignOutViewModel = hiltViewModel(),
    analyticsViewModel: SignOutAnalyticsViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
) {
    val loading by viewModel.loadingState.collectAsState()
    val context = LocalActivity.current as FragmentActivity

    EdgeToEdgePage { _ ->
        if (loading) {
            LoadingScreen(loadingAnalyticsViewModel) {
                context.finishAndRemoveTask()
            }
        } else {
            SignOutBody(
                onClose = {
                    analyticsViewModel.trackCloseIcon()
                    viewModel.goBack()
                },
                onBack = {
                    analyticsViewModel.trackBackPressed()
                    viewModel.goBack()
                },
                onPrimary = {
                    analyticsViewModel.trackPrimary()
                    viewModel.signOut()
                },
            )
            analyticsViewModel.trackSignOutView()
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
internal fun SignOutBody(
    onPrimary: () -> Unit,
    onClose: () -> Unit,
    onBack: () -> Unit,
) {
    FullScreenDialogue(
        onDismissRequest = onClose,
        onBack = onBack,
    ) { scrollState ->
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = smallPadding)) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(scrollState)
                        .weight(1f)
                        .semantics(true) { }
                        .focusGroup(),
            ) {
                GdsHeading(
                    text = stringResource(id = R.string.app_signOutConfirmationTitle),
                    textAlign = GdsHeadingAlignment.LeftAligned,
                )
                Text(
                    text = stringResource(id = R.string.app_signOutConfirmationBody1),
                    modifier = Modifier.padding(vertical = smallPadding),
                )
                val listTitle = stringResource(R.string.app_signOutConfirmationSubtitle)
                GdsBulletedList(
                    bulletListItems =
                        persistentListOf(
                            ListItem(text = stringResource(R.string.app_signOutConfirmationBullet1)),
                            ListItem(text = stringResource(R.string.app_signOutConfirmationBullet2)),
                            ListItem(text = stringResource(R.string.app_signOutConfirmationBullet3)),
                        ),
                    title = ListTitle(text = listTitle, titleType = TitleType.Text),
                    modifier = Modifier.padding(),
                )
                Text(
                    text = stringResource(R.string.app_signOutConfirmationBody3),
                    modifier = Modifier.padding(top = smallPadding),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = smallPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GdsButton(
                    text = stringResource(R.string.app_signOutAndDeleteAppDataButton),
                    buttonType = ButtonTypeV2.Destructive(),
                    onClick = onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@PreviewScreenSizes
@Preview
@Composable
internal fun SignOutPreview() {
    GdsTheme {
        SignOutBody(
            onPrimary = {},
            onClose = {},
            onBack = {},
        )
    }
}
