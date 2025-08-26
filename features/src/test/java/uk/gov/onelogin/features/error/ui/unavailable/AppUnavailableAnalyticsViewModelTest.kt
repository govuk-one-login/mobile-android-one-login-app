package uk.gov.onelogin.features.error.ui.unavailable

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
import uk.gov.onelogin.core.utils.GAUtils
import uk.gov.onelogin.core.utils.GAUtils.IS_ERROR_REASON_TRUE
import uk.gov.onelogin.core.utils.GAUtils.TRUE

@RunWith(AndroidJUnit4::class)
class AppUnavailableAnalyticsViewModelTest {
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var reason: String
    private lateinit var iconText: String
    private lateinit var logger: AnalyticsLogger
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var viewModel: AppUnavailableAnalyticsViewModel

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        logger = mock()
        requiredParameters =
            RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
            )
        name = context.getEnglishString(R.string.app_appUnavailableTitle)
        id = context.getEnglishString(R.string.app_unavailable_page_id)
        reason = context.getEnglishString(R.string.app_unavailable_error_reason)
        iconText = context.getEnglishString(R.string.system_backButton)
        viewModel = AppUnavailableAnalyticsViewModel(context, logger)
    }

    @Test
    fun trackAppUnavailableViewLogsScreenView() {
        // Given a ViewEvent.Screen
        val event =
            ViewEvent.Error(
                name = name,
                id = id,
                endpoint = "",
                status = "",
                reason = reason,
                params = requiredParameters
            )
        // When tracking the app unavailable screen view
        viewModel.trackUnavailableView()
        // Then log a ScreenView to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)

        assertThat(
            IS_ERROR_REASON_TRUE,
            GAUtils.containsIsError(event, TRUE)
        )
    }

    @Test
    fun trackBackButtonLogsTrackEventIcon() {
        // Given a TrackEvent.Icon
        val event =
            TrackEvent.Icon(
                text = iconText,
                params = requiredParameters
            )
        // When tracking the hardware back button
        viewModel.trackBackButton()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }
}
