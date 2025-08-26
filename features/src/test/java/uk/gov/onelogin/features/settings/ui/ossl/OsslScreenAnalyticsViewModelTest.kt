package uk.gov.onelogin.features.settings.ui.ossl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent

@RunWith(AndroidJUnit4::class)
class OsslScreenAnalyticsViewModelTest {
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var backButton: String
    private lateinit var backIcon: String
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var logger: AnalyticsLogger
    private lateinit var viewModel: OsslAnalyticsViewModel
    private lateinit var crashLogger: Logger

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        requiredParameters =
            RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
                taxonomyLevel3 = TaxonomyLevel3.LICENCES
            )
        logger = mock()
        crashLogger = mock()
        name = context.getEnglishString(R.string.app_osslTitle)
        id = context.getEnglishString(R.string.ossl_page_id)
        backButton = context.getEnglishString(R.string.system_backButton)
        backIcon = context.getEnglishString(R.string.system_backIcon)
        viewModel = OsslAnalyticsViewModel(context, logger, crashLogger)
    }

    @Test
    fun trackScreen() {
        // Given a ViewEvent.Screen
        val event =
            ViewEvent.Screen(
                name = name,
                id = id,
                params = requiredParameters
            )
        // When tracking the home screen
        viewModel.trackScreen()
        // Then log a ScreenView to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackEventLink() {
        val domain = "testUrl"
        val title = "testTile"
        // Given a TrackEvent.Link
        val event =
            TrackEvent.Link(
                isExternal = true,
                domain = domain,
                text = title,
                params = requiredParameters
            )
        // When tracking redirect to "Using your ..." web page
        viewModel.trackLink(
            title,
            domain
        )
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackBackButton() {
        // Given a TrackEvent.Icon
        val event =
            TrackEvent.Icon(
                text = backButton,
                params = requiredParameters
            )
        // When tracking the hardware back button
        viewModel.trackBackButton()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackBackIcon() {
        // Given a TrackEvent.Icon
        val event =
            TrackEvent.Icon(
                text = backIcon,
                params = requiredParameters
            )
        // When tracking the hardware back button
        viewModel.trackBackIcon()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun logError() {
        val tag = "tag"
        val message = "crash"
        val exception = Exception(message)
        viewModel.logError(tag, message, exception)

        verify(crashLogger).error(tag, message, exception)
    }
}
