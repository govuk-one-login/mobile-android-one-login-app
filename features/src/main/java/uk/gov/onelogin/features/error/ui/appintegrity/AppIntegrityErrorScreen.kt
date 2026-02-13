package uk.gov.onelogin.features.error.ui.appintegrity

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import uk.gov.android.ui.theme.m3.GdsTheme

@Composable
fun AppIntegrityErrorScreen(
    goBack: () -> Unit = {},
){
    GdsTheme {
        BackHandler(true) {

        }
    }

}