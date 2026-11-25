package uk.gov.onelogin.developer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.android.onelogin.core.R

class DeveloperExtraTabsTest {

    @Test
    fun `the developer panel gains a mock network tab`() {
        assertEquals(
            listOf(R.string.app_developer_tab_mock_network),
            developerExtraTabs.map { it.title },
        )
    }
}
