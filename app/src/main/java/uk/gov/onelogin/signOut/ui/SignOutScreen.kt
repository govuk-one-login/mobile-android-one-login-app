package uk.gov.onelogin.signOut.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.pages.AlertPage
import uk.gov.android.ui.pages.AlertPageParameters

@Composable
fun SignOutScreen(
    goBack: () -> Unit = { },
    goToSignIn: () -> Unit = { },
    viewModel: SignOutViewModel = hiltViewModel()
) {
    val context = LocalContext.current as FragmentActivity
    AlertPage(alertPageParameters = AlertPageParameters(
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
            goBack()
        },
        onPrimary = {
            viewModel.signOut(context)
            goToSignIn()
        }
    ))
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

@Composable
@Preview
private fun Preview() {
    SignOutScreen()
}