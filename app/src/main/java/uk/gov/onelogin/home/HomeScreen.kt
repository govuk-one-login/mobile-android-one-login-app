package uk.gov.onelogin.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.gov.onelogin.components.appbar.GdsTopAppBar
import uk.gov.onelogin.components.navigation.GdsNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    /* DCMAW-7031: Configure top app bar: */
    GdsTopAppBar(
        title = {
            Text(text = "This should change")
        },
    ).generate()

    /* DCMAW-7045: Configure bottom navigation bar: */
    GdsNavigationBar(items = listOf()).generate()
}
