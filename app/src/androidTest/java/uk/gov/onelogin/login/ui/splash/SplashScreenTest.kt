package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLocalLogin
import uk.gov.onelogin.login.usecase.HandleLoginRedirect
import uk.gov.onelogin.login.usecase.HandleRemoteLogin
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.login.usecase.UseCaseModule
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.optin.BinderModule
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.optin.ui.NOTICE_TAG

@HiltAndroidTest
@UninstallModules(
    UseCaseModule::class,
    NavigatorModule::class,
    AppInfoApiModule::class,
    BinderModule::class
)
class SplashScreenTest : TestCase() {
    @BindValue
    val verifyIdToken: VerifyIdToken = mock()

    @BindValue
    val handleLocalLogin: HandleLocalLogin = mock()

    @BindValue
    val handleRemoteLogin: HandleRemoteLogin = mock()

    @BindValue
    val handleLoginRedirect: HandleLoginRedirect = mock()

    @BindValue
    val mockNavigator: Navigator = mock()

    @BindValue
    val mockSaveTokens: SaveTokens = mock()

    @BindValue
    val appInfoService: AppInfoService = mock()

    @BindValue
    val appInfoLocalSource: AppInfoLocalSource = mock()

    @BindValue
    val analyticsRepo: OptInRepository = mock()

    @BindValue
    val optInLocalSource: OptInLocalSource = mock()

    @BindValue
    val optInRemoteSource: OptInRemoteSource = mock()

    private lateinit var splashIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher
    private lateinit var privacyNotice: SemanticsMatcher
    private lateinit var loadingIndicator: SemanticsMatcher
    private lateinit var loadingText: SemanticsMatcher

    @Before
    fun setUp() {
        hiltRule.inject()

        wheneverBlocking { appInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(TestUtils.appInfoData))

        splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
        unlockButton = hasText(resources.getString(R.string.app_unlockButton))
        privacyNotice = hasTestTag(NOTICE_TAG)
        loadingIndicator = hasContentDescription(
            resources.getString(R.string.app_splashScreenLoadingContentDescription)
        )
        loadingText = hasText(resources.getString(R.string.app_splashScreenLoadingIndicatorText))
    }

    @Test
    fun verifySplashScreen() {
        wheneverBlocking {
            analyticsRepo.isOptInPreferenceRequired()
        }.thenReturn(flow { emit(false) })
        // Given
        composeTestRule.setContent {
            SplashScreen()
        }

        // Then
        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
    }

    @Test
    fun testUnlockButton() {
        wheneverBlocking {
            analyticsRepo.isOptInPreferenceRequired()
        }.thenReturn(flow { emit(false) })
        wheneverBlocking { handleLocalLogin.invoke(any(), any()) }.thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UserCancelled)
        }

        // Given
        composeTestRule.setContent {
            SplashScreen()
        }
        composeTestRule.waitUntil(15000) {
            composeTestRule.onNode(unlockButton).isDisplayed()
        }

        wheneverBlocking { handleLocalLogin.invoke(any(), any()) }.thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
        }

        // When
        composeTestRule.onNode(unlockButton).performClick()

        // Then
        verify(mockNavigator).goBack()
        verify(mockNavigator).navigate(LoginRoutes.Welcome, false)
    }

    @Test
    fun verifyPreview() {
        composeTestRule.setContent {
            SplashScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
    }

    @Test
    fun verifyUnlockPreview() {
        composeTestRule.setContent {
            UnlockScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsDisplayed()
        composeTestRule.onNode(loadingText).assertIsNotDisplayed()
        composeTestRule.onNode(loadingIndicator).assertIsNotDisplayed()
    }

    @Test
    fun testLoadingPreview() {
        composeTestRule.setContent {
            LoadingSplashScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(loadingText).assertIsDisplayed()
        composeTestRule.onNode(loadingIndicator).assertIsDisplayed()
    }
}
