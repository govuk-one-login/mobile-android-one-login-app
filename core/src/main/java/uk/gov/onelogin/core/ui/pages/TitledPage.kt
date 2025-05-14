@file:OptIn(ExperimentalMaterial3Api::class)

package uk.gov.onelogin.core.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import uk.gov.android.ui.components.m3.HeadingSize
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.components.DIVIDER_TEST_TAG
import uk.gov.onelogin.core.ui.components.FlexibleTopBar
import uk.gov.onelogin.core.ui.components.FlexibleTopBarColors

@Composable
fun TitledPage(
    title: Int,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FlexibleTopBar(
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = smallPadding)
                            .statusBarsPadding()
                    ) {
                        Text(
                            text = stringResource(title),
                            style = HeadingSize.HeadlineLarge().style(),
                            modifier = Modifier.fillMaxWidth().padding(bottom = smallPadding),
                            textAlign = TextAlign.Center
                        )
                        HorizontalDivider(Modifier.testTag(DIVIDER_TEST_TAG))
                    }
                },
                colors =
                FlexibleTopBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        content(paddingValues)
    }
}
