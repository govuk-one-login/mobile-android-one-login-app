package uk.gov.onelogin.ui.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.extensions.domain
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.onelogin.TrackEventTestCase
import uk.gov.onelogin.executeTrackEventTestCase

class SettingsAnalyticsViewModelTest {
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var logger: AnalyticsLogger
    private lateinit var viewModel: SettingsAnalyticsViewModel
    val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        requiredParameters = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
            taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
        )
        logger = mock()
        viewModel = SettingsAnalyticsViewModel(context, logger)
    }

    @Test
    fun testSettingsScreenTrackEvents() {
        // Given a TrackEvent
        listOf(
            TrackEventTestCase.Screen(
                trackFunction = {
                    viewModel.trackSettingsView()
                },
                name = context.getEnglishString(R.string.action_settings),
                id = context.getEnglishString(R.string.settings_page_id)
            ),
            TrackEventTestCase.Icon(
                trackFunction = {
                    viewModel.trackBackButton()
                },
                text = context.getEnglishString(R.string.system_backButton)
            ),
            TrackEventTestCase.Button(
                trackFunction = {
                    viewModel.trackSignOutButton()
                },
                text = context.getEnglishString(R.string.app_signOutButton)
            ),
            TrackEventTestCase.Button(
                trackFunction = {
                    viewModel.trackOpenSourceButton()
                },
                text = context.getEnglishString(R.string.app_openSourceLicences)
            ),
            TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackSignInDetailLink()
                },
                domain = context.getEnglishString(R.string.app_manageSignInDetailsUrl).domain,
                text = context.getEnglishString(R.string.app_settingsSignInDetailsLink)
            ),
            TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackUsingOneLoginLink()
                },
                domain = context.getEnglishString(R.string.app_oneLoginCardLinkUrl).domain,
                text = context.getEnglishString(R.string.app_appGuidanceLink)
            ),
            TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackContactOneLoginLink()
                },
                domain = context.getEnglishString(R.string.app_contactUrl).domain,
                text = context.getEnglishString(R.string.app_contactLink)
            ),
            TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackPrivacyNoticeLink()
                },
                domain = context.getEnglishString(R.string.privacy_notice_url).domain,
                text = context.getEnglishString(R.string.app_privacyNoticeLink2)
            ),
            TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackAccessibilityStatementLink()
                },
                domain = context.getEnglishString(R.string.app_accessibilityStatementUrl).domain,
                text = context.getEnglishString(R.string.app_accessibilityStatement)
            )
        ).forEach {
            // When tracking
           val result = executeTrackEventTestCase(it, requiredParameters)
            // Then log a event to the AnalyticsLogger
            verify(logger).logEventV3Dot1(result)
        }
    }
}
