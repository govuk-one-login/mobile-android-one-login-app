@file:OptIn(ExperimentalMaterial3Api::class)

package uk.gov.onelogin.core.ui.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import uk.gov.android.ui.components.m3.HeadingSize
import uk.gov.android.ui.theme.smallPadding
import uk.gov.ui.components.appbar.GdsTopAppBar

@Composable
fun TitledPage(
    title: Int,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            GdsTopAppBar(
                title = {
                    Text(
                        text = stringResource(title),
                        style = HeadingSize.HeadlineLarge().style(),
                        modifier = Modifier
                            .padding(vertical = smallPadding)
                            .semantics { heading() },
                        textAlign = TextAlign.Center
                    )
                },
                colors = {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                },
                scrollBehavior = scrollBehavior
            ).generate()
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        content(paddingValues)
    }
}
