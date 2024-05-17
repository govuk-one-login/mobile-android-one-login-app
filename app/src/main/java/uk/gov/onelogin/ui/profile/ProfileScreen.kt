package uk.gov.onelogin.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.onelogin.R
import uk.gov.android.ui.pages.TitledPage
import uk.gov.android.ui.pages.TitledPageParameters
import uk.gov.onelogin.ui.components.EmailHeader

@Composable
fun ProfileScreen() {
    TitledPage(
        parameters = TitledPageParameters(
            R.string.app_profile
        ) {
            EmailHeader("someEmail@mail.com")
        }
    )
}

@Composable
@Preview
private fun Preview() {
    ProfileScreen()
}
