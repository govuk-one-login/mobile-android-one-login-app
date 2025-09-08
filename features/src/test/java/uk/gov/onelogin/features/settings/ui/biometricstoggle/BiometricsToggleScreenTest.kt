package uk.gov.onelogin.features.settings.ui.biometricstoggle

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.analytics.parameters.data.Type
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@RunWith(AndroidJUnit4::class)
class BiometricsToggleScreenTest : FragmentActivityTestCase() {
    private lateinit var featureFlags: FeatureFlags
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var navigator: Navigator
    private lateinit var saveTokens: SaveTokens
    private lateinit var viewModel: BiometricsToggleScreenViewModel
    private lateinit var logger: AnalyticsLogger
    private lateinit var analyticsViewModel: BiometricsToggleAnalyticsViewModel

    private val title = hasText(context.getString(R.string.app_biometricsToggleTitle))
    private val navIcon = hasContentDescription(context.getString(R.string.app_back_icon))
    private val toggle = hasTestTag(context.getString(R.string.optInSwitchTestTag))
    private val bulletListTitle = hasText(
        context.getString(R.string.app_biometricsToggleBody1Wallet)
    )
    private val bullet1 = hasText(context.getString(R.string.app_biometricsToggleBullet1))
    private val bullet2 = hasText(context.getString(R.string.app_biometricsToggleBullet2))
    private val body2Wallet = hasText(
        context.getString(R.string.app_biometricsToggleBody2Wallet)
    )
    private val subtitle = hasText(context.getString(R.string.app_biometricsToggleSubtitle))
    private val body3Wallet = hasText(
        context.getString(R.string.app_biometricsToggleBody3Wallet)
    )
    private val body1 = hasText(context.getString(R.string.app_biometricsToggleBody1))
    private val body2 = hasText(context.getString(R.string.app_biometricsToggleBody2))
    private val body3 = hasText(context.getString(R.string.app_biometricsToggleBody3))

    @Before
    fun setup() {
        featureFlags = mock()
        localAuthManager = mock()
        navigator = mock()
        saveTokens = mock()
        viewModel = BiometricsToggleScreenViewModel(
            featureFlags = featureFlags,
            localAuthManager = localAuthManager,
            navigator = navigator,
            saveTokens = saveTokens
        )
        logger = mock()
        analyticsViewModel = BiometricsToggleAnalyticsViewModel(context, logger)
    }

    @Test
    fun bodyDisplayedWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)
        val event = ViewEvent.Screen(
            name = context.getEnglishString(R.string.app_biometricsToggleTitle),
            id = context.getEnglishString(R.string.biometrics_toggle_wallet_id),
            params = requiredParams
        )
        setContent()

        composeTestRule.apply {
            onAllNodes(title).assertCountEquals(2)
            onNode(navIcon).assertIsDisplayed().assertHasClickAction()
            onNode(toggle, useUnmergedTree = true).assertIsToggleable()
            onNode(bulletListTitle).assertIsDisplayed()
            onNode(bullet1).assertIsDisplayed()
            onNode(bullet2).assertIsDisplayed()
            onNode(body2Wallet).assertIsDisplayed()
            onNode(subtitle).assertIsDisplayed()
            onNode(body3Wallet).assertIsDisplayed()
        }

        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun bodyDisplayedNoWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        val event = ViewEvent.Screen(
            name = context.getEnglishString(R.string.app_biometricsToggleTitle),
            id = context.getEnglishString(R.string.biometrics_toggle_no_wallet_id),
            params = requiredParams
        )
        setContent()

        composeTestRule.apply {
            onAllNodes(title).assertCountEquals(2)
            onNode(toggle, useUnmergedTree = true).assertIsToggleable()
            onNode(body1).assertIsDisplayed()
            onNode(body2).assertIsDisplayed()
            onNode(subtitle).assertIsDisplayed()
            onNode(body3).assertIsDisplayed()
        }

        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun testToggleEnabled() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        // The event would still log the enabled after because we are mocking the
        // localAuthPref to always return enabled
        val eventEnabled = TrackEvent.Form(
            text = context.getEnglishString(R.string.app_biometricsToggleLabel),
            response = true.toString(),
            params = requiredParams,
            type = Type.Toggle
        )
        setContent()

        composeTestRule.apply {
            onNode(toggle, useUnmergedTree = true).performClick()
        }

        verify(localAuthManager).toggleBiometrics()
        verify(logger).logEventV3Dot1(eventEnabled)
    }

    @Test
    fun testToggleDisabled() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        // The event would still log the disabled after because we are mocking the
        // localAuthPref to always return disabled
        val eventDisabled = TrackEvent.Form(
            text = context.getEnglishString(R.string.app_biometricsToggleLabel),
            response = true.toString(),
            params = requiredParams,
            type = Type.Toggle
        )
        setContent()

        composeTestRule.apply {
            onNode(toggle, useUnmergedTree = true).performClick()
        }

        verify(localAuthManager).toggleBiometrics()
        verify(logger).logEventV3Dot1(eventDisabled)
    }

    @Test
    fun testBack() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        val event = TrackEvent.Icon(
            text = context.getEnglishString(R.string.app_back_icon),
            params = requiredParams
        )
        setContent()

        composeTestRule.apply {
            onNode(navIcon).performClick()
        }

        verify(navigator).goBack()
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun testBackSystem() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        val event = TrackEvent.Button(
            text = context.getEnglishString(R.string.system_backButton),
            params = requiredParams
        )
        setContent()

        Espresso.pressBack()

        verify(navigator).goBack()
        verify(logger).logEventV3Dot1(event)
    }

    @Test
    fun testWalletPreview() {
        composeTestRule.setContent {
            BiometricsToggleEnabledWalletBodyPreview()
        }

        composeTestRule.apply {
            onAllNodes(title).assertCountEquals(2)
            onNode(navIcon).assertIsDisplayed().assertHasClickAction()
            onNode(toggle, useUnmergedTree = true).assertIsToggleable()
            onNode(bulletListTitle).assertIsDisplayed()
            onNode(bullet1).assertIsDisplayed()
            onNode(bullet2).assertIsDisplayed()
            onNode(body2Wallet).assertIsDisplayed()
            onNode(subtitle).assertIsDisplayed()
            onNode(body3Wallet).assertIsDisplayed()
        }
    }

    @Test
    fun testNoWalletPreview() {
        composeTestRule.setContent {
            BiometricsToggleDisabledNoWalletBodyPreview()
        }

        composeTestRule.apply {
            onAllNodes(title).assertCountEquals(2)
            onNode(toggle, useUnmergedTree = true).assertIsToggleable()
            onNode(body1).assertIsDisplayed()
            onNode(body2).assertIsDisplayed()
            onNode(subtitle).assertIsDisplayed()
            onNode(body3).assertIsDisplayed()
        }
    }

    fun setContent() {
        composeTestRule.setContent {
            BiometricsToggleScreen(viewModel, analyticsViewModel)
        }
    }

    companion object {
        private val requiredParams = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.SETTINGS,
            taxonomyLevel3 = TaxonomyLevel3.BIOMETRICS_TOGGLE
        )
    }
}
