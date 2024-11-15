package uk.gov.onelogin.signOut.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.android.onelogin.R
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule
import uk.gov.onelogin.features.FeaturesModule
import uk.gov.onelogin.features.WalletFeatureFlag
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.signOut.SignOutModule
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.ui.error.ErrorRoutes
import uk.gov.onelogin.wallet.DeleteWalletDataUseCase
import uk.gov.onelogin.wallet.WalletModule

@HiltAndroidTest
@UninstallModules(
    SignOutModule::class,
    NavigatorModule::class,
    WalletModule::class,
    AnalyticsModule::class,
    FeaturesModule::class
)
class SignOutScreenTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

    @BindValue
    val signOutUseCase: SignOutUseCase = mock()

    @BindValue
    val walletSdk: WalletSdk = mock()

    @BindValue
    val analytics: AnalyticsLogger = mock()

    @BindValue
    val deleteWalletDataUseCase: DeleteWalletDataUseCase = mock()

    @BindValue
    val featureFlags: FeatureFlags = InMemoryFeatureFlags()

    private val title = hasText(resources.getString(R.string.app_signOutConfirmationTitle))
    private val ctaButton = hasText(resources.getString(R.string.app_signOutAndDeleteAppDataButton))
    private val closeButton = hasContentDescription("Close")

    @Before
    fun setupNavigation() {
        hiltRule.inject()
    }

    @Test
    fun verifyScreenDisplayedWallet() {
        (featureFlags as InMemoryFeatureFlags).plusAssign(setOf(WalletFeatureFlag.ENABLED))
        composeTestRule.setContent {
            SignOutScreen()
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(
            SignOutAnalyticsViewModel.makeSignOutWalletViewEvent(
                context
            )
        )
    }

    @Test
    fun verifyScreenDisplayedNoWallet() {
        (featureFlags as InMemoryFeatureFlags).minusAssign(setOf(WalletFeatureFlag.ENABLED))
        composeTestRule.setContent {
            SignOutScreen()
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(
            SignOutAnalyticsViewModel.makeSignOutNoWalletViewEvent(
                context
            )
        )
    }

    @Test
    fun verifySignOutButtonSucceeds() = runBlocking {
        composeTestRule.setContent {
            SignOutScreen()
        }
        composeTestRule.onNode(ctaButton).performClick()

        verify(analytics).logEventV3Dot1(SignOutAnalyticsViewModel.onPrimaryEvent(context))
        verify(signOutUseCase).invoke(any())
        verify(mockNavigator).navigate(LoginRoutes.Root, true)
    }

    @Test
    fun verifySignOutButtonFails() = runTest {
        composeTestRule.setContent {
            SignOutScreen()
        }
        whenever(signOutUseCase.invoke(any()))
            .thenThrow(SignOutError(Exception("something went wrong")))
        composeTestRule.onNode(ctaButton).performClick()
        verify(signOutUseCase).invoke(any())
        verify(mockNavigator).navigate(ErrorRoutes.SignOut)
    }

    @Test
    fun verifyCloseIconButton() {
        composeTestRule.setContent {
            SignOutScreen()
        }
        composeTestRule.onNode(closeButton).performClick()

        verify(analytics).logEventV3Dot1(SignOutAnalyticsViewModel.onCloseIcon())
        verify(mockNavigator).goBack()
    }

    @Test
    fun previewWalletTest() {
        composeTestRule.setContent {
            SignOutWalletPreview()
        }
    }

    @Test
    fun previewNoWalletTest() {
        composeTestRule.setContent {
            SignOutNoWalletPreview()
        }
    }
}
