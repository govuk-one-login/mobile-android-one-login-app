package uk.gov.onelogin.features.settings.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.LibraryDefaults
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.pages.TitledPage

@Composable
fun OsslScreen() {
    GdsTheme {
        TitledPage(R.string.app_osslTitle) {
            OsslAboutLibrariesScreen(it)
        }
    }
}

@Composable
fun OsslAboutLibrariesScreen(
    padding: PaddingValues
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
        license?.url?.also {
            try {
                uriHandler.openUri(it)
            } catch (e: IllegalArgumentException) {
                Log.e("OsslScreen", "Failed to open url: $it", e)
            }
        }
    }
}
