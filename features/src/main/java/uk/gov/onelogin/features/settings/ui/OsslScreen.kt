package uk.gov.onelogin.features.settings.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.LibraryDefaults
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.pages.TitledPage

@Composable
fun OsslScreen(
    analyticsViewModel: OsslAnalyticsViewModel = hiltViewModel()
) {
    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }

    GdsTheme {
        TitledPage(R.string.app_osslTitle) {
            OsslAboutLibrariesScreen(it) { title, url ->
                analyticsViewModel.trackLink(title, url)
            }
        }
    }
}

@Composable
fun OsslAboutLibrariesScreen(
    padding: PaddingValues,
    onClick: (String, String) -> Unit
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
                Log.e("OsslScreen", "Failed to open url: $it", e)
            }
        }
    }
}
