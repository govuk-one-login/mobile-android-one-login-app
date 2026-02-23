package uk.gov.onelogin.features.error.ui.appintegrity

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
import uk.gov.logging.api.v3dot1.model.ViewEvent
import uk.gov.onelogin.core.utils.GAUtils
import uk.gov.onelogin.core.utils.GAUtils.IS_ERROR_REASON_TRUE
import uk.gov.onelogin.core.utils.GAUtils.TRUE

@RunWith(AndroidJUnit4::class)
class AppIntegrityAnalyticsViewModelTest {
    private lateinit var name: String

    private lateinit var id: String

    private lateinit var reason: String

    private lateinit var requiredParams: RequiredParameters

    private lateinit var analyticsLogger: AnalyticsLogger

    private lateinit var viewModel: AppIntegrityErrorAnalyticsViewModel

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        name = context.getEnglishString(R.string.app_appIntegrityErrorTitle)
        id = context.getEnglishString(R.string.app_integrity_error_firebase_screen_id)
        reason = context.getEnglishString(R.string.app_integrity_error_reason)

        requiredParams =
            RequiredParameters(
                taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
                taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
            )
        analyticsLogger = mock()
        viewModel =
            AppIntegrityErrorAnalyticsViewModel(
                context,
                analyticsLogger
            )
    }

    @Test
    fun trackScreen() {
        val event =
            ViewEvent.Error(
                name = name,
                id = id,
                endpoint = "",
                reason = reason,
                status = "",
                params = requiredParams
            )

        viewModel.trackScreen()

        verify(analyticsLogger).logEventV3Dot1(event)

        assertThat(
            IS_ERROR_REASON_TRUE,
            GAUtils.containsIsError(event, TRUE)
        )
    }
}
