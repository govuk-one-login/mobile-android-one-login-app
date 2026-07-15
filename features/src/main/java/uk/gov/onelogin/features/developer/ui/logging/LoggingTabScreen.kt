package uk.gov.onelogin.features.developer.ui.logging

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.patterns.leftalignedscreen.LeftAlignedScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun LoggingTabScreen(viewModel: LoggingTabViewModel = hiltViewModel()) {
    LoggingTabScreenContent(
        onCrash = { viewModel.crash() },
        onLogError = { viewModel.logError() },
        onLogInfo = { viewModel.logInfo() },
    )
}

@Composable
internal fun LoggingTabScreenContent(
    onCrash: () -> Unit = {},
    onLogError: () -> Unit = {},
    onLogInfo: () -> Unit = {},
) {
    LeftAlignedScreen(
        body = { horizontalPadding ->
            item {
                GdsButton(
                    text = stringResource(R.string.app_developer_logging_crash),
                    buttonType = ButtonTypeV2.Destructive(),
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    onClick = onCrash,
                )
            }
            item {
                GdsButton(
                    text = stringResource(R.string.app_developer_logging_error),
                    buttonType = ButtonTypeV2.Secondary(),
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    onClick = onLogError,
                )
            }
            item {
                GdsButton(
                    text = stringResource(R.string.app_developer_logging_info),
                    buttonType = ButtonTypeV2.Secondary(),
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    onClick = onLogInfo,
                )
            }
        }
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun LoggingTabScreenPreview() {
    GdsTheme {
        LoggingTabScreenContent()
    }
}
