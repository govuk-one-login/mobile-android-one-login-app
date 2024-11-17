package uk.gov.onelogin.ui.components

import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule

@HiltAndroidTest
@UninstallModules(
    NavigatorModule::class
)
class BackHandlerWithPopTest : TestCase() {
    @BindValue
    val navigator: Navigator = mock()

    @Test
    fun backBehaviourTest() {
        var backPressed = false

        composeTestRule.setContent {
            BackHandlerWithPop {
                backPressed = true
            }
        }

        composeTestRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()

            assertTrue(backPressed)
            verify(navigator).goBack()
        }
    }

    @Test
    fun backBehaviourTestNotEnabled() {
        var backPressed = false

        composeTestRule.setContent {
            BackHandlerWithPop(false) {
                backPressed = true
            }
        }

        composeTestRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()

            assertFalse(backPressed)
            verifyNoInteractions(navigator)
        }
    }
}
