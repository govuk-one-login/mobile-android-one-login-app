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
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.toPersistentList
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
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUIState

@Composable
fun SignOutScreen(
    viewModel: SignOutViewModel = hiltViewModel(),
    analyticsViewModel: SignOutAnalyticsViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel()
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
                uiState = viewModel.uiState,
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
                }
            )
            analyticsViewModel.trackSignOutView(viewModel.uiState)
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
internal fun SignOutBody(
    uiState: SignOutUIState,
    onPrimary: () -> Unit,
    onClose: () -> Unit,
    onBack: () -> Unit
) {
    FullScreenDialogue(
        onDismissRequest = onClose,
        onBack = onBack
    ) { scrollState ->
        val bulletList = uiState.bullets.map { ListItem(stringResource(it)) }
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = smallPadding)) {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
                    .weight(1f)
                    .semantics(true) { }
                    .focusGroup()
            ) {
                GdsHeading(
                    text = stringResource(id = uiState.title),
                    textAlign = GdsHeadingAlignment.LeftAligned
                )
                Text(
                    text = stringResource(id = uiState.header),
                    modifier = Modifier.padding(vertical = smallPadding)
                )
                val listTitle = uiState.subTitle?.let { stringResource(it) }
                GdsBulletedList(
                    bulletListItems = bulletList.toPersistentList(),
                    title = listTitle?.let {
                        ListTitle(
                            text = listTitle,
                            titleType = TitleType.Text
                        )
                    },
                    modifier = Modifier.padding()
                )
                Text(
                    text = stringResource(uiState.footer),
                    modifier = Modifier.padding(top = smallPadding)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = smallPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GdsButton(
                    text = stringResource(uiState.button),
                    buttonType = uiState.buttonType,
                    onClick = onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
internal fun SignOutWalletPreview() {
    GdsTheme {
        SignOutBody(
            uiState = SignOutUIState.Wallet,
            onPrimary = {},
            onClose = {},
            onBack = {}
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SignOutPreview() {
    GdsTheme {
        SignOutBody(
            uiState = SignOutUIState.NoWallet,
            onPrimary = {},
            onClose = {},
            onBack = {}
        )
    }
}
