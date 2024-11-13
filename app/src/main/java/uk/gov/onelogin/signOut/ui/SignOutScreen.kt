package uk.gov.onelogin.signOut.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.pages.AlertPage
import uk.gov.android.ui.pages.AlertPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.meta.ScreenPreview

@Composable
fun SignOutScreen(
    viewModel: SignOutViewModel = hiltViewModel()
) {
    val onBackPressedDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val analytics: SignOutAnalyticsViewModel = hiltViewModel()
    // Needed for deleteWalletData
    val fragmentActivity = LocalContext.current as FragmentActivity
    SignOutBody(
        onClose = {
            analytics.trackCloseIcon()
            viewModel.goBack()
        },
        onPrimary = {
            analytics.trackPrimary()
            viewModel.signOut(fragmentActivity)
        }
    )
    analytics.trackSignOutView()

    BackHandler {
        println("pressing back")
        analytics.trackBackPressed()
        onBackPressedDispatcher?.onBackPressed()
    }
}

@Composable
internal fun SignOutBody(
    onClose: () -> Unit = {},
    onPrimary: () -> Unit = {}
) {
    AlertPage(
        alertPageParameters = AlertPageParameters(
            title = R.string.app_signOutConfirmationTitle,
            annotatedContent = buildAnnotatedString {
                append(stringResource(id = R.string.app_signOutConfirmationBody1))
                appendBulletLine(stringResource(id = R.string.app_signOutConfirmationBullet1))
                appendBulletLine(stringResource(id = R.string.app_signOutConfirmationBullet2))
                appendBulletLine(stringResource(id = R.string.app_signOutConfirmationBullet3))
                appendLine()
                appendBoldLine(stringResource(id = R.string.app_signOutConfirmationBody2))
                appendLine()
                append(stringResource(id = R.string.app_signOutConfirmationBody3))
            },
            ctaText = R.string.app_signOutAndDeleteAppDataButton,
            onClose = {
                onClose()
            },
            onPrimary = {
                onPrimary()
            }
        )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SignOutPreview() {
    GdsTheme {
        SignOutBody()
    }
}

private fun AnnotatedString.Builder.appendBulletLine(string: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        appendLine()
        append("\t\t•\t\t")
    }
    append(string)
}

private fun AnnotatedString.Builder.appendBoldLine(string: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        appendLine()
        append(string)
        appendLine()
    }
}
