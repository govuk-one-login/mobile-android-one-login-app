package uk.gov.onelogin.features.settings.ui.ossl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.GdsIconButtonDefaults
import uk.gov.android.ui.componentsv2.topappbar.GdsTopAppBar
import uk.gov.android.ui.theme.m3.GdsLocalColorScheme
import uk.gov.android.ui.theme.m3.GdsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsslScreen(
    analyticsViewModel: OsslAnalyticsViewModel = hiltViewModel(),
    onBackBehaviour: () -> Unit = {}
) {
    BackHandler {
        analyticsViewModel.trackBackButton()
        onBackBehaviour()
    }
    LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    GdsTheme {
        Scaffold(
            topBar = {
                GdsTopAppBar(
                    title = stringResource(R.string.app_osslTitle),
                    navigationButton = GdsIconButtonDefaults.defaultBackContent(),
                    onClick = {
                        analyticsViewModel.trackBackIcon()
                        onBackBehaviour()
                    },
                    scrollBehaviour = scrollBehaviour
                )
            },
            modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection)
        ) {
            OsslAboutLibrariesScreen(
                padding = it,
                onClick = { title, url ->
                    analyticsViewModel.trackLink(title, url)
                },
                onLogError = { tag, message, exception ->
                    analyticsViewModel.logError(tag, message, exception)
                }
            )
        }
    }
}

@Composable
fun OsslAboutLibrariesScreen(
    padding: PaddingValues,
    onClick: (String, String) -> Unit,
    onLogError: (String, String, Exception) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val background = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    LibrariesContainer(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        colors = LibraryDefaults.libraryColors(
            backgroundColor = background,
            badgeBackgroundColor = primary
        )
    ) { library ->
        val license = library.licenses.firstOrNull()
        onClick(
            library.name,
            license?.url ?: "no url"
        )
        license?.url?.also {
            try {
                uriHandler.openUri(it)
            } catch (e: IllegalArgumentException) {
                onLogError(e.javaClass.simpleName, "Failed to open url: $it", e)
            }
        }
    }
}
