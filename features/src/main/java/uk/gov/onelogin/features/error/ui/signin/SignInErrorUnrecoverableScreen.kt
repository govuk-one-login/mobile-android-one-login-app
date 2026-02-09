package uk.gov.onelogin.features.error.ui.signin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.images.GdsIcon
import uk.gov.android.ui.patterns.errorscreen.v2.ErrorScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.android.ui.theme.meta.ScreenPreview
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.utils.ModifierExtensions.errorBodyItemModifier

@Composable
fun SignInErrorUnrecoverableScreen(
    analyticsViewModel: SignInErrorAnalyticsViewModel = hiltViewModel(),
    viewModel: SignInErrorUnrecoverableViewModel = hiltViewModel(),
) {
    GdsTheme {
        val context = LocalActivity.current as Activity
        BackHandler(true) {
            analyticsViewModel.trackBackButton()
            viewModel.exitApp(context)
        }
        LaunchedEffect(Unit) { analyticsViewModel.trackUnrecoverableScreen() }
        EdgeToEdgePage { _ ->
            SignInErrorBody()
        }
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
private fun SignInErrorBody() {
    val bodyContent =
        persistentListOf(
            stringResource(R.string.app_genericErrorPageBody),
        )

    ErrorScreen(
        icon = { padding ->
            GdsIcon(
                image = ImageVector.vectorResource(uk.gov.android.ui.patterns.R.drawable.ic_warning_error),
                contentDescription = stringResource(uk.gov.android.ui.patterns.R.string.error_icon_description),
                modifier = errorBodyItemModifier(padding)
            )
        },
        title = { padding ->
            GdsHeading(
                text = stringResource(R.string.app_signInErrorTitle),
                modifier = errorBodyItemModifier(padding),
                textAlign = GdsHeadingAlignment.CenterAligned,
            )
        },
        body = { padding ->
            items(bodyContent.size) { index ->
                Text(
                    text = bodyContent[index],
                    textAlign = TextAlign.Center,
                    modifier = errorBodyItemModifier(padding),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Preview
@Composable
fun SignInErrorUnrecoverableScreenPreview() {
    GdsTheme {
        SignInErrorBody()
    }
}
