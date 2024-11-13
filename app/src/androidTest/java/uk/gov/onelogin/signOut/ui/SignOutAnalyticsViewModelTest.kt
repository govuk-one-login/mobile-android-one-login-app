package uk.gov.onelogin.signOut.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent

class SignOutAnalyticsViewModelTest {
    private lateinit var buttonText: String
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var logger: AnalyticsLogger
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var viewModel: SignOutAnalyticsViewModel

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        logger = mock()
        requiredParameters = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.ACCOUNT,
            taxonomyLevel3 = TaxonomyLevel3.SIGN_OUT
        )
        buttonText = context.getEnglishString(R.string.app_signOutAndDeleteAppDataButton)
        name = context.getEnglishString(R.string.app_signOutConfirmationTitle)
        id = context.getEnglishString(R.string.sign_out_wallet_page_id)
        viewModel = SignOutAnalyticsViewModel(context, logger)
    }

    @Test
    fun trackSignOutLogsTrackPrimary() {
        // Given a TrackEvent.Link
        val event = TrackEvent.Button(
            text = buttonText,
            params = requiredParameters
        )
        // When tracking re-auth
        viewModel.trackPrimary()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackSignOutLogsTrackCloseIcon() {
        // Given a TrackEvent.Link
        val event = TrackEvent.Icon(
            text = "back",
            params = requiredParameters
        )
        // When tracking re-auth
        viewModel.trackCloseIcon()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackSignOutLogsTrackBackPressed() {
        // Given a TrackEvent.Link
        val event = TrackEvent.Icon(
            text = "back - system",
            params = requiredParameters
        )
        // When tracking re-auth
        viewModel.trackBackPressed()
        // Then log a TrackEvent to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackSignOutViewLogsViewEventScreen() {
        // Given a ViewEvent.Screen
        val event = ViewEvent.Screen(
            name = name,
            id = id,
            params = requiredParameters
        )
        // When tracking the signed out info screen view
        viewModel.trackSignOutView()
        // Then log a ScreenView to the AnalyticsLogger
        verify(logger).logEventV3Dot1(event)
    }
}
