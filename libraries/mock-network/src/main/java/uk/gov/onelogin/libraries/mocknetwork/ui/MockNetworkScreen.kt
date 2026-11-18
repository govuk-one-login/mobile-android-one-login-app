package uk.gov.onelogin.libraries.mocknetwork.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.patterns.leftalignedscreen.LeftAlignedScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

/**
 * Developer/test screen that lets the current [MockNetworkScenario] be changed at runtime.
 *
 * It is intended to be hosted in the app's developer menu in the mocked build so that automated
 * (e.g. Maestro) tests can switch the mocked network behaviour mid-journey. Each scenario button
 * carries a stable test tag of the form `mockNetworkScenario_<SCENARIO_NAME>`.
 */
@Composable
fun MockNetworkScreen(viewModel: MockNetworkViewModel = hiltViewModel()) {
    val selected by viewModel.scenario.collectAsStateWithLifecycle()
    MockNetworkContent(
        selectedScenario = selected,
        onScenarioSelected = viewModel::selectScenario,
    )
}

@Composable
internal fun MockNetworkContent(
    selectedScenario: MockNetworkScenario,
    onScenarioSelected: (MockNetworkScenario) -> Unit = {},
) {
    LeftAlignedScreen(
        title = { horizontalPadding ->
            GdsHeading(
                text = "Mock Network (Test Build)",
                textAlign = GdsHeadingAlignment.LeftAligned,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        },
        body = { horizontalPadding ->
            MockNetworkScenario.entries.forEach { scenario ->
                item {
                    GdsButton(
                        text = scenario.displayName(selected = scenario == selectedScenario),
                        buttonType = if (scenario == selectedScenario) {
                            ButtonTypeV2.Primary()
                        } else {
                            ButtonTypeV2.Secondary()
                        },
                        modifier = Modifier
                            .padding(horizontal = horizontalPadding)
                            .testTag("mockNetworkScenario_${scenario.name}"),
                        onClick = { onScenarioSelected(scenario) },
                    )
                }
            }
        },
    )
}

private fun MockNetworkScenario.displayName(selected: Boolean): String {
    val label = name.lowercase().replace('_', ' ')
    return if (selected) "✓ $label" else label
}

@ExcludeFromJacocoGeneratedReport
@Preview(showBackground = true)
@Composable
internal fun MockNetworkPreview() {
    GdsTheme {
        MockNetworkContent(selectedScenario = MockNetworkScenario.EMPTY_SUCCESS)
    }
}
