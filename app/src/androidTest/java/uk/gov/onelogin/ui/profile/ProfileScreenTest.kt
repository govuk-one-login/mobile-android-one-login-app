package uk.gov.onelogin.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.signOut.SignOutRoutes

@HiltAndroidTest
@UninstallModules(NavigatorModule::class)
class ProfileScreenTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

    private val yourDetailsHeader = hasText(resources.getString(R.string.app_profileSubtitle1))
    private val yourDetailsTitle = hasText(resources.getString(R.string.app_signInDetails))
    private val yourDetailsSubTitle =
        hasText(resources.getString(R.string.app_manageSignInDetailsFootnote))

    private val legalHeader = hasText(resources.getString(R.string.app_profileSubtitle2))
    private val legalLink1 = hasText(resources.getString(R.string.app_privacyNoticeLink2))
    private val legalLink2 = hasText(resources.getString(R.string.app_OpenSourceLicences))
    private val signOutButton = hasText(resources.getString(R.string.app_signOutButton))

    @Before
    fun initialisesHomeScreenProfileScreen() {
        composeTestRule.setContent {
            ProfileScreen()
        }
    }

    @Test
    fun yourDetailsGroupDisplayed() {
        composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
    }

    @Test
    fun legalGroupDisplayed() {
        composeTestRule.onNode(legalHeader).assertIsDisplayed()
        composeTestRule.onNode(legalLink1).assertIsDisplayed()
        composeTestRule.onNode(legalLink2).assertIsDisplayed()
    }

    @Test
    fun signOutCta() {
        composeTestRule.onNode(signOutButton).performClick()
        verify(mockNavigator).navigate(SignOutRoutes.Start)
    }
}
