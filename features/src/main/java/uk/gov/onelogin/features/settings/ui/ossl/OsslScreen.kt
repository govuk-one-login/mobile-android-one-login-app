package uk.gov.onelogin.features.settings.ui.ossl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import uk.gov.android.onelogin.core.R as CoreR
import uk.gov.android.onelogin.features.R
import uk.gov.android.ui.componentsv2.button.GdsIconButtonDefaults
import uk.gov.android.ui.componentsv2.topappbar.GdsTopAppBar
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsslScreen(
    analyticsViewModel: OsslAnalyticsViewModel = hiltViewModel(),
    onBackBehaviour: () -> Unit = {},
) {
    BackHandler {
        analyticsViewModel.trackBackButton()
        onBackBehaviour()
    }
    LaunchedEffect(Unit) { analyticsViewModel.trackScreen() }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val libraries by produceLibraries(R.raw.aboutlibraries)
    GdsTheme {
        Scaffold(
            topBar = {
                GdsTopAppBar(
                    title = stringResource(CoreR.string.app_osslTitle),
                    navigationButton = GdsIconButtonDefaults.defaultBackContent(),
                    onClick = {
                        analyticsViewModel.trackBackIcon()
                        onBackBehaviour()
                    },
                    scrollBehaviour = scrollBehaviour,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        ) {
            OsslAboutLibrariesScreen(
                padding = it,
                libraries = libraries,
                onClick = { title, url ->
                    analyticsViewModel.trackLink(title, url)
                },
                onLogError = { tag, message, exception ->
                    analyticsViewModel.logError(tag, message, exception)
                },
            )
        }
    }
}

@Composable
fun OsslAboutLibrariesScreen(
    padding: PaddingValues,
    libraries: Libs?,
    onClick: (String, String) -> Unit,
    onLogError: (String, String, Exception) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    LibrariesContainer(
        modifier =
            Modifier
                .padding(padding)
                .fillMaxSize(),
        libraries = libraries,
        onLibraryClick = { library ->
            val license = library.licenses.firstOrNull()
            onClick(
                library.name,
                license?.url ?: "no url",
            )
            license?.url?.also {
                try {
                    uriHandler.openUri(it)
                } catch (e: IllegalArgumentException) {
                    onLogError(e.javaClass.simpleName, "Failed to open url: $it", e)
                }
            }
            true
        }
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun OsslAboutLibrariesScreenPreview() {
    GdsTheme {
        OsslAboutLibrariesScreen(
            padding = PaddingValues(),
            libraries = previewLibraries(),
            onClick = { _, _ -> },
            onLogError = { _, _, _ -> },
        )
    }
}

@ExcludeFromJacocoGeneratedReport
internal fun previewLibraries(): Libs {
    val apache = License(
        name = "Apache License 2.0",
        url = "https://spdx.org/licenses/Apache-2.0.html",
        spdxId = "Apache-2.0",
        hash = "apache-2.0",
    )
    val mit = License(
        name = "MIT License",
        url = "https://spdx.org/licenses/MIT.html",
        spdxId = "MIT",
        hash = "mit",
    )
    return Libs(
        libraries = listOf(
            Library(
                uniqueId = "com.example:library-one",
                artifactVersion = "1.0.0",
                name = "Library One",
                description = "A useful library for doing things",
                website = "https://example.com/library-one",
                developers = emptyList(),
                organization = null,
                scm = null,
                licenses = setOf(apache),
            ),
            Library(
                uniqueId = "org.sample:library-two",
                artifactVersion = "2.3.1",
                name = "Library Two",
                description = "Another helpful dependency",
                website = "https://example.com/library-two",
                developers = emptyList(),
                organization = null,
                scm = null,
                licenses = setOf(mit),
            ),
            Library(
                uniqueId = "io.testing:library-three",
                artifactVersion = "0.9.5",
                name = "Library Three",
                description = "A testing utility library",
                website = "https://example.com/library-three",
                developers = emptyList(),
                organization = null,
                scm = null,
                licenses = setOf(apache),
            ),
        ),
        licenses = setOf(apache, mit),
    )
}
