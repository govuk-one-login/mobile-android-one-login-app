package uk.gov.onelogin.features.error.ui.appintegrity

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.onelogin.features.error.ui.offline.OfflineErrorAnalyticsViewModel

@RunWith(AndroidJUnit4::class)
class AppIntegrityAnalyticsViewModelTest {
    private lateinit var name: String

    private lateinit var id: String

    private lateinit var reason: String

    private lateinit var button: String

    private lateinit var backButton: String

    private lateinit var requiredParams: RequiredParameters

    private lateinit var analyticsLogger: AnalyticsLogger

    private lateinit var viewModel: OfflineErrorAnalyticsViewModel
}
