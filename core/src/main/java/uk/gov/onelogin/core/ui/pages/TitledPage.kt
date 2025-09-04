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
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.theme.m3.GdsLocalColorScheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.components.DIVIDER_TEST_TAG
import uk.gov.onelogin.core.ui.components.FlexibleTopBar
import uk.gov.onelogin.core.ui.components.FlexibleTopBarColors

@OptIn(UnstableDesignSystemAPI::class)
@Composable
fun TitledPage(
    title: Int,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Surface {
        Scaffold(
            topBar = {
                FlexibleTopBar(
                    content = {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = smallPadding)
                                .statusBarsPadding()
                        ) {
                            GdsHeading(
                                text = stringResource(title),
                                modifier = Modifier.fillMaxWidth().padding(bottom = smallPadding),
                                style = GdsHeadingStyle.Title2
                            )
                            HorizontalDivider(Modifier.testTag(DIVIDER_TEST_TAG))
                        }
                    },
                    colors =
                    FlexibleTopBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = GdsLocalColorScheme.current
                            .topBarScrolledBackground
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}
