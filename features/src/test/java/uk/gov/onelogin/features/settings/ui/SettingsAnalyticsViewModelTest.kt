package uk.gov.onelogin.features.settings.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.domain
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.ViewEvent
import uk.gov.onelogin.core.utils.GAUtils
import uk.gov.onelogin.core.utils.GAUtils.FALSE
import uk.gov.onelogin.core.utils.GAUtils.IS_ERROR_REASON_FALSE
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.TestUtils.executeTrackEventTestCase

@RunWith(AndroidJUnit4::class)
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
    fun testScreenEvent() {
        val event = TestUtils.TrackEventTestCase.Screen(
            trackFunction = {
                viewModel.trackSettingsView()
            },
            name = context.getEnglishString(R.string.action_settings),
            id = context.getEnglishString(R.string.settings_page_id)
        )
        val result = executeTrackEventTestCase(event, requiredParameters)
        // Then log a event to the AnalyticsLogger
        verify(logger).logEventV3Dot1(result)

        assertThat(
            IS_ERROR_REASON_FALSE,
            GAUtils.containsIsError(result as ViewEvent, FALSE)
        )
    }

    @Test
    fun testButtonEvents() {
        listOf(
            TestUtils.TrackEventTestCase.Button(
                trackFunction = {
                    viewModel.trackSignOutButton()
                },
                text = context.getEnglishString(R.string.app_signOutButton)
            ),
            TestUtils.TrackEventTestCase.Button(
                trackFunction = {
                    viewModel.trackOpenSourceButton()
                },
                text = context.getEnglishString(R.string.app_openSourceLicences)
            )
        ).forEach {
            // When tracking
            val result = executeTrackEventTestCase(it, requiredParameters)
            // Then log a event to the AnalyticsLogger
            verify(logger).logEventV3Dot1(result)
        }
    }

    @Test
    @Suppress("LongMethod")
    fun testLinkEvents() {
        // Given a TrackEvent
        listOf(
            TestUtils.TrackEventTestCase.Icon(
                trackFunction = {
                    viewModel.trackBackButton()
                },
                text = context.getEnglishString(R.string.system_backButton)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackSignInDetailLink()
                },
                domain = context.getEnglishString(R.string.app_manageSignInDetailsUrl).domain,
                text = context.getEnglishString(R.string.app_settingsSignInDetailsLink)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackUsingOneLoginLink()
                },
                domain = context.getEnglishString(R.string.app_oneLoginCardLinkUrl).domain,
                text = context.getEnglishString(R.string.app_proveYourIdentityLink)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackContactOneLoginLink()
                },
                domain = context.getEnglishString(R.string.app_contactUrl).domain,
                text = context.getEnglishString(R.string.app_contactLink)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackPrivacyNoticeLink()
                },
                domain = context.getEnglishString(R.string.privacy_notice_url).domain,
                text = context.getEnglishString(R.string.app_privacyNoticeLink2)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackAccessibilityStatementLink()
                },
                domain = context.getEnglishString(R.string.app_accessibilityStatementUrl).domain,
                text = context.getEnglishString(R.string.app_accessibilityStatement)
            ),
            TestUtils.TrackEventTestCase.Button(
                trackFunction = { viewModel.trackBiometricsButton() },
                text = context.getEnglishString(R.string.app_settingsBiometricsField)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackAddDocumentsLink()
                },
                domain = context.getEnglishString(R.string.app_add_document_url).domain,
                text = context.getEnglishString(R.string.app_addDocumentsLink)
            ),
            TestUtils.TrackEventTestCase.Link(
                trackFunction = {
                    viewModel.trackTermsAndConditionsLink()
                },
                domain = context.getEnglishString(R.string.app_terms_and_conditions_url).domain,
                text = context.getEnglishString(R.string.app_termsAndConditionsLink)
            )
        ).forEach {
            // When tracking
            val result = executeTrackEventTestCase(it, requiredParameters)
            // Then log a event to the AnalyticsLogger
            verify(logger).logEventV3Dot1(result)
        }
    }
}
