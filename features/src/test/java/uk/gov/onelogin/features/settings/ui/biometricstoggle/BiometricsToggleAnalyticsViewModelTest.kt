package uk.gov.onelogin.features.settings.ui.biometricstoggle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.analytics.parameters.data.Type
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.TestUtils.executeTrackEventTestCase

@RunWith(AndroidJUnit4::class)
class BiometricsToggleAnalyticsViewModelTest {
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var logger: AnalyticsLogger
    private lateinit var viewModel: BiometricsToggleAnalyticsViewModel
    val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        requiredParameters =
            RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
                taxonomyLevel3 = TaxonomyLevel3.BIOMETRICS_TOGGLE
            )
        logger = mock()
        viewModel = BiometricsToggleAnalyticsViewModel(context, logger)
    }

    @Test
    fun testWalletScreenEvent() {
        val event =
            TestUtils.TrackEventTestCase.Screen(
                trackFunction = {
                    viewModel.trackWalletCopyView()
                },
                name = context.getEnglishString(R.string.app_biometricsToggleTitle),
                id = context.getEnglishString(R.string.biometrics_toggle_wallet_id)
            )
        val result = executeTrackEventTestCase(event, requiredParameters)
        // Then log a event to the AnalyticsLogger
        verify(logger).logEventV3Dot1(result)
    }

    @Test
    fun testNoWalletScreenEvent() {
        val event =
            TestUtils.TrackEventTestCase.Screen(
                trackFunction = {
                    viewModel.trackNoWalletCopyView()
                },
                name = context.getEnglishString(R.string.app_biometricsToggleTitle),
                id = context.getEnglishString(R.string.biometrics_toggle_no_wallet_id)
            )
        val result = executeTrackEventTestCase(event, requiredParameters)
        // Then log a event to the AnalyticsLogger
        verify(logger).logEventV3Dot1(result)
    }

    @Test
    fun testBackButtonsAndToggle() {
        listOf(
            TestUtils.TrackEventTestCase.Button(
                trackFunction = {
                    viewModel.trackBackButton()
                },
                text = context.getEnglishString(R.string.system_backButton)
            ),
            TestUtils.TrackEventTestCase.Icon(
                trackFunction = {
                    viewModel.trackIconBackButton()
                },
                text = context.getEnglishString(R.string.app_back_icon)
            ),
            TestUtils.TrackEventTestCase.Form(
                trackFunction = { viewModel.trackToggleEvent(true) },
                text = context.getEnglishString(R.string.app_biometricsToggleLabel),
                type = Type.Toggle,
                response = true.toString()
            )
        ).forEach {
            // When tracking
            val result = executeTrackEventTestCase(it, requiredParameters)
            // Then log a event to the AnalyticsLogger
            verify(logger).logEventV3Dot1(result)
        }
    }
}
