package uk.gov.onelogin.features.unit.home.idcheck.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.testdouble.analytics.FakeAnalyticsLogger
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModalAnalyticsViewModel

@RunWith(AndroidJUnit4::class)
class HowToProveYourIdentityModalAnalyticsViewModelTest {
    private val analyticsLogger = FakeAnalyticsLogger()
    private val viewModel = HowToProveYourIdentityModalAnalyticsViewModel(
        ApplicationProvider.getApplicationContext(),
        analyticsLogger
    )

    @Test
    fun `it tracks the screen view event`() {
        viewModel.trackScreen()

        val actualEvent = analyticsLogger.filter { it.isScreenView() }.firstOrNull()

        assertEquals(
            AnalyticsEvent(
                "screen_view",
                mapOf(
                    "is_error" to "false",
                    "screen_id" to "a1d4edc2-2491-4970-a135-90ff0896de23",
                    "screen_class" to "how to prove your identity",
                    "screen_name" to "how to prove your identity",
                    "saved_doc_type" to "undefined",
                    "primary_publishing_organisation" to "government digital service - digital identity",
                    "organisation" to "<OT1056>",
                    "taxonomy_level1" to "one login mobile application",
                    "taxonomy_level2" to "home",
                    "taxonomy_level3" to "undefined",
                    "language" to "en",
                ),
            ),
            actualEvent,
        )
    }
}
