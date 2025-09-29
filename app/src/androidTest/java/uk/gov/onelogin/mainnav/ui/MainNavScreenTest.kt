package uk.gov.onelogin.mainnav.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.onelogin.core.R
import uk.gov.android.wallet.core.R as walletR
import uk.gov.logging.api.CrashLogger
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.AnalyticsModule
import uk.gov.onelogin.featureflags.FeaturesModule
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetter
import uk.gov.onelogin.features.wallet.data.WalletRepository
import uk.gov.onelogin.utils.TestCase
import uk.gov.onelogin.wallet.WalletRepositoryModule

@HiltAndroidTest
@UninstallModules(FeaturesModule::class, AnalyticsModule::class, WalletRepositoryModule::class)
class MainNavScreenTest : TestCase() {
    private val homeTab = hasText(resources.getString(R.string.app_home))
    private val walletTab = hasText(resources.getString(R.string.app_TabBarWallet))
    private val settingsTab = hasText(resources.getString(R.string.app_settings))

    @BindValue
    val featureFlagSetter: FeatureFlagSetter = mock()

    @BindValue
    val featureFlags: FeatureFlags = mock()

    @BindValue
    var analytics: AnalyticsLogger = mock()

    @BindValue
    val logger: Logger = mock()

    @BindValue
    val crashLogger: CrashLogger = mock()

    @BindValue
    val walletRepository: WalletRepository = mock()

    @Before
    fun setup() {
        navController = TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
    }

    @Test
    fun checkBottomOptionsDisplayed() = runBlocking {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(false)
        setupUi()

        composeTestRule.onNode(homeTab).apply {
            isDisplayed()
            performClick()
        }

        composeTestRule.waitUntil(5000L) { composeTestRule.onNode(walletTab).isDisplayed() }
        composeTestRule.onNode(walletTab).isDisplayed()
        composeTestRule.onNode(settingsTab).isDisplayed()

        assertEquals(
            BottomNavDestination.Home.key,
            navController.currentDestination?.route
        )
        verify(analytics).logEventV3Dot1(MainNavAnalyticsViewModel.makeHomeButtonEvent(context))
    }

    @Test
    fun goesToWalletOnClick() {
        whenever(featureFlags[any()]).thenReturn(true)
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(false)
        setupUi()

        composeTestRule.waitUntil(5000L) { composeTestRule.onNode(walletTab).isDisplayed() }
        composeTestRule.onNode(walletTab).performClick()
        assertTrue(
            navController.currentDestination?.route?.contains(
                BottomNavDestination.Wallet.key
            ) ?: false
        )

        composeTestRule.onNodeWithText(
            resources.getString(walletR.string.intro_card_title)
        ).assertIsDisplayed()

        verify(analytics).logEventV3Dot1(MainNavAnalyticsViewModel.makeWalletButtonEvent(context))
    }

    @Test
    fun checkWalletNotDisplayed() {
        whenever(featureFlags[any()]).thenReturn(false)
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(false)
        setupUi()
        composeTestRule.onAllNodes(homeTab)[1].isDisplayed() // we have double match of `Home` text
        composeTestRule.onNode(walletTab).assertDoesNotExist()
        composeTestRule.onNode(settingsTab).isDisplayed()

        assertEquals(
            BottomNavDestination.Home.key,
            navController.currentDestination?.route
        )
    }

    @Test
    fun goesToSettingsOnClick() {
        whenever(featureFlags[any()]).thenReturn(false)
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(false)
        setupUi()
        composeTestRule.onNode(settingsTab).performClick()

        assertEquals(
            BottomNavDestination.Settings.key,
            navController.currentDestination?.route
        )

        composeTestRule.onAllNodes(settingsTab).assertCountEquals(2)

        verify(analytics).logEventV3Dot1(MainNavAnalyticsViewModel.makeSettingsButtonEvent(context))
    }

    @Ignore("This is failing because the nav graph has not be 'set' in the time")
    @Test
    fun deeplinkExists() {
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(true)
        whenever(featureFlags[any()]).thenReturn(true)
        setupUi()

        assertTrue(
            navController.currentDestination?.route?.contains(
                BottomNavDestination.Wallet.key
            ) ?: false
        )
    }

    private fun setupUi() {
        composeTestRule.setContent {
            MainNavScreen(navController)
        }
    }
}
