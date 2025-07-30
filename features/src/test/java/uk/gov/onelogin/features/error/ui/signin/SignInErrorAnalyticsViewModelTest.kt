package uk.gov.onelogin.features.error.ui.signin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
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
class SignInErrorAnalyticsViewModelTest {
    private lateinit var name: String
    private lateinit var recoverableId: String
    private lateinit var unrecoverableId: String
    private lateinit var recoverableReason: String
    private lateinit var unrecoverableReason: String
    private lateinit var button: String
    private lateinit var backButton: String
    private lateinit var requiredParams: RequiredParameters
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var viewModel: SignInErrorAnalyticsViewModel

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        name = context.getEnglishString(R.string.app_signInErrorTitle)
        recoverableId = context.getEnglishString(R.string.sign_in_recoverable_error_screen_id)
        unrecoverableId = context.getEnglishString(R.string.sign_in_unrecoverable_error_screen_id)
        recoverableReason = context.getEnglishString(R.string.sign_in_error_recoverable_reason)
        unrecoverableReason = context.getEnglishString(R.string.sign_in_error_unrecoverable_reason)
        button = context.getEnglishString(R.string.app_closeButton)
        backButton = context.getEnglishString(R.string.system_backButton)
        requiredParams =
            RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.LOGIN,
                taxonomyLevel3 = TaxonomyLevel3.ERROR
            )
        analyticsLogger = mock()
        viewModel =
            SignInErrorAnalyticsViewModel(
                context,
                analyticsLogger
            )
    }

    @Test
    fun trackRecoverableScreen() {
        val event =
            ViewEvent.Error(
                name = name,
                id = recoverableId,
                endpoint = "",
                reason = recoverableReason,
                status = "",
                params = requiredParams
            )

        viewModel.trackRecoverableScreen()

        verify(analyticsLogger).logEventV3Dot1(event)

        assertThat(
            IS_ERROR_REASON_TRUE,
            GAUtils.containsIsError(event, TRUE)
        )
    }

    @Test
    fun trackUnrecoverableScreen() {
        val event =
            ViewEvent.Error(
                name = name,
                id = unrecoverableId,
                endpoint = "",
                reason = unrecoverableReason,
                status = "",
                params = requiredParams
            )

        viewModel.trackUnrecoverableScreen()

        verify(analyticsLogger).logEventV3Dot1(event)

        assertThat(
            IS_ERROR_REASON_TRUE,
            GAUtils.containsIsError(event, TRUE)
        )
    }

    @Test
    fun trackButton() {
        val event =
            TrackEvent.Button(
                text = button,
                params = requiredParams
            )

        viewModel.trackButton()

        verify(analyticsLogger).logEventV3Dot1(event)
    }

    @Test
    fun trackBackButton() {
        val event =
            TrackEvent.Icon(
                text = backButton,
                params = requiredParams
            )

        viewModel.trackBackButton()

        verify(analyticsLogger).logEventV3Dot1(event)
    }
}
