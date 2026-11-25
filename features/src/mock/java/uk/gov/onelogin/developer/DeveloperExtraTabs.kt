package uk.gov.onelogin.developer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.developer.ui.TabItem
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkScreen

/**
 * The mocked environment adds a tab for choosing the mocked network's scenario, so an automated
 * test can change the mocked responses mid-journey.
 */
val developerExtraTabs: List<TabItem> = listOf(
    TabItem(R.string.app_developer_tab_mock_network, Icons.Filled.Share) {
        MockNetworkScreen()
    },
)
