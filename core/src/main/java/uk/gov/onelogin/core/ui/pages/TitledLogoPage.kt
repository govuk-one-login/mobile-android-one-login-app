@file:OptIn(ExperimentalMaterial3Api::class)

package uk.gov.onelogin.core.ui.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.spacingDouble
import uk.gov.ui.components.appbar.GdsTopAppBar

@Composable
fun TitledLogoPage(
    @DrawableRes logo: Int,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            GdsTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.padding(vertical = spacingDouble),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(logo),
                            contentDescription = stringResource(
                                R.string.one_login_image_content_desc
                            ),
                            modifier = Modifier.semantics { heading() }
                        )
                    }
                },
                colors = {
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = getAppBarColor(),
                        scrolledContainerColor = getAppBarColor()
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

@Composable
private fun getAppBarColor(): Color {
    return colorResource(
        if (isSystemInDarkTheme()) {
            R.color.govuk_dark_blue
        } else {
            R.color.govuk_blue
        }
    )
}
