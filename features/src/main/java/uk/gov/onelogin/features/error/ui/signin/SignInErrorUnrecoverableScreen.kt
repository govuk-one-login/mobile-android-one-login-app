package uk.gov.onelogin.features.error.ui.signin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
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
import uk.gov.android.ui.patterns.R as patternsR

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
                image = ImageVector.vectorResource(patternsR.drawable.ic_warning_error),
                contentDescription = stringResource(patternsR.string.error_icon_description),
                modifier = Modifier.errorBodyItemModifier(padding)
            )
        },
        title = { padding ->
            GdsHeading(
                text = stringResource(R.string.app_signInErrorTitle),
                modifier = Modifier.errorBodyItemModifier(padding),
                textAlign = GdsHeadingAlignment.CenterAligned,
            )
        },
        body = { padding ->
            items(bodyContent.size) { index ->
                Text(
                    text = bodyContent[index],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.errorBodyItemModifier(padding),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
fun SignInErrorUnrecoverableScreenPreview() {
    GdsTheme {
        SignInErrorBody()
    }
}
