package uk.gov.onelogin.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.openid.appauth.TokenResponse
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.R
import uk.gov.onelogin.components.appbar.GdsTopAppBar
import uk.gov.onelogin.components.navigation.GdsNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tokens: TokenResponse? = null
) {
    GdsTheme {
        Column {
            /* DCMAW-7031: Configure top app bar: */
            GdsTopAppBar(
                title = {
                    GdsHeading(
                        headingParameters = HeadingParameters(
                            size = HeadingSize.H1(),
                            text = R.string.homeScreenTitle
                        )
                    )
                }
            ).generate()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Access Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    tokens?.accessToken ?: "No access token set!",
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag("homeScreen-accessToken")
                )
                Text(
                    text = "ID Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tokens?.idToken ?: "No id token set!",
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag("homeScreen-idToken")
                )
                Text(
                    text = "Refresh Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tokens?.refreshToken ?: "No refresh token set!",
                    modifier = Modifier
                        .padding(
                            all = 16.dp
                        )
                        .testTag("homeScreen-refreshToken")
                )
            }
            /* DCMAW-7045: Configure bottom navigation bar: */
            GdsNavigationBar(items = listOf()).generate()
        }
    }
}

@Composable
@Preview
fun Preview() {
    HomeScreen()
}
