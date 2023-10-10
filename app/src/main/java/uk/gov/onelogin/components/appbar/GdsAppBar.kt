package uk.gov.onelogin.components.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wrapper data class for the [TopAppBar] Composable.
 *
 * - [Developer guide](https://developer.android.com/jetpack/compose/components/app-bars)
 * - [Material 3 design](https://m3.material.io/components/top-app-bar/overview)
 */
@OptIn(ExperimentalMaterial3Api::class)
data class GdsTopAppBar
constructor(
    private val title: @Composable () -> Unit,
    private val modifier: Modifier = Modifier,
    private val actions: @Composable RowScope.() -> Unit = {},
    private val colors: @Composable () -> TopAppBarColors = { TopAppBarDefaults.topAppBarColors() },
    private val navigationIcon: @Composable () -> Unit = {},
    private val scrollBehavior: TopAppBarScrollBehavior? = null,
    private val windowInsets: @Composable () -> WindowInsets = { TopAppBarDefaults.windowInsets },
) {
    @OptIn(ExperimentalMaterial3Api::class)
    val generate: @Composable () -> Unit
        get() = {
            TopAppBar(
                title = title,
                modifier = modifier,
                actions = actions,
                colors = colors(),
                navigationIcon = navigationIcon,
                scrollBehavior = scrollBehavior,
                windowInsets = windowInsets(),
            )
        }
}
