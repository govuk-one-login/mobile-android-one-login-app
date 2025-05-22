package uk.gov.onelogin.features.settings.ui.biomtericsoptin

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
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
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.settings.ui.biometricsoptin.BiometricsOptInScreen
import uk.gov.onelogin.features.settings.ui.biometricsoptin.BiometricsOptInScreenViewModel

@RunWith(AndroidJUnit4::class)
class BiometricsOptInScreenTest : FragmentActivityTestCase() {
    private lateinit var featureFlags: FeatureFlags
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var navigator: Navigator
    private lateinit var viewModel: BiometricsOptInScreenViewModel

    private val title = hasText(context.getString(R.string.app_biometricsOptInToggleTitle))
    private val navIcon = hasContentDescription(context.getString(R.string.app_back_icon))
    private val toggle = hasTestTag(context.getString(R.string.optInSwitchTestTag))
    private val bulletListTitle = hasText(
        context.getString(R.string.app_biometricsOptInToggleBody1Wallet)
    )
    private val bullet1 = hasText(context.getString(R.string.app_biometricsOptInToggleBullet1))
    private val bullet2 = hasText(context.getString(R.string.app_biometricsOptInToggleBullet2))
    private val body2Wallet = hasText(
        context.getString(R.string.app_biometricsOptInToggleBody2Wallet)
    )
    private val subtitle = hasText(context.getString(R.string.app_biometricsOptInToggleSubtitle))
    private val body3Wallet = hasText(
        context.getString(R.string.app_biometricsOptInToggleBody3Wallet)
    )
    private val body1 = hasText(context.getString(R.string.app_biometricsOptInToggleBody1))
    private val body2 = hasText(context.getString(R.string.app_biometricsOptInToggleBody2))
    private val body3 = hasText(context.getString(R.string.app_biometricsOptInToggleBody3))

    @Before
    fun setup() {
        featureFlags = mock()
        localAuthManager = mock()
        navigator = mock()
        viewModel = BiometricsOptInScreenViewModel(featureFlags, localAuthManager, navigator)
    }

    @Test
    fun bodyDisplayedWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)
        composeTestRule.setContent {
            BiometricsOptInScreen(viewModel)
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
    fun bodyDisplayedNoWallet() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        composeTestRule.setContent {
            BiometricsOptInScreen(viewModel)
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

    @Test
    fun testToggle() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        composeTestRule.setContent {
            BiometricsOptInScreen(viewModel)
        }

        composeTestRule.apply {
            onNode(toggle, useUnmergedTree = true).performClick()
        }

        verify(localAuthManager).toggleBiometrics()
    }

    @Test
    fun testBack() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
        composeTestRule.setContent {
            BiometricsOptInScreen(viewModel)
        }

        composeTestRule.apply {
            onNode(navIcon).performClick()
        }

        verify(navigator).goBack()
    }
}
