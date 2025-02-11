package uk.gov.onelogin.login.ui.splash

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent

class SplashScreenAnalyticsViewModelTest {
    private lateinit var context: Context
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var nameUnlock: String
    private lateinit var idUnlock: String
    private lateinit var unlockButton: String
    private lateinit var backButton: String
    private lateinit var logger: AnalyticsLogger
    private lateinit var requiredParameters: RequiredParameters
    private lateinit var viewModel: SplashScreenAnalyticsViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        logger = mock()
        requiredParameters = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
            taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
        )
        name = context.getString(R.string.app_splashScreenAnalyticsScreenName)
        id = context.getString(R.string.splash_screen_page_id)
        nameUnlock = context.getString(R.string.app_splashScreenUnlockAnalyticsScreenName)
        idUnlock = context.getString(R.string.splash_unlock_screen_page_id)
        unlockButton = context.getString(R.string.app_unlockButton)
        backButton = context.getString(R.string.system_bottomNavigation_backButton)
        viewModel = SplashScreenAnalyticsViewModel(context, logger)
    }

    @Test
    fun trackSplashScreen() {
        val event = ViewEvent.Screen(
            name = name,
            id = id,
            params = requiredParameters
        )

        viewModel.trackSplashScreen(context, isLocked = false)

        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackSplashUnlockScreen() {
        val event = ViewEvent.Screen(
            name = nameUnlock,
            id = idUnlock,
            params = requiredParameters
        )

        viewModel.trackSplashScreen(context, isLocked = true)

        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackUnlockButton() {
        val event = TrackEvent.Button(
            text = unlockButton,
            params = requiredParameters
        )

        viewModel.trackUnlockButton()

        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun trackBackButton() {
        val event = TrackEvent.Icon(
            text = backButton,
            params = requiredParameters
        )

        viewModel.trackBackButton()

        verify(logger).logEventV3Dot1(event)
    }
}
