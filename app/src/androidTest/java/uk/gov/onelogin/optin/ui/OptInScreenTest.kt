package uk.gov.onelogin.optin.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.optin.domain.repository.OptInRepository

@HiltAndroidTest
class OptInScreenTest : TestCase() {
    private lateinit var privacyNotice: SemanticsMatcher
    private lateinit var primaryButton: SemanticsMatcher
    private lateinit var textButton: SemanticsMatcher
    private val repository: OptInRepository = mock()
    private lateinit var viewModel: OptInViewModel

    @Before
    fun setUp() {
        privacyNotice = hasTestTag(NOTICE_TAG)
        primaryButton = hasText(resources.getString(R.string.app_shareAnalyticsButton))
        textButton = hasText(resources.getString(R.string.app_doNotShareAnalytics))
        wheneverBlocking { repository.optIn() }.thenAnswer { }
        wheneverBlocking { repository.optOut() }.thenAnswer { }
        viewModel = OptInViewModel(repository)
    }

    @Test
    fun shareAnalytics() {
        // Given the OptInScreen Composable
        var actual = false
        composeTestRule.setContent {
            OptInScreen(viewModel) { actual = true }
        }
        // When clicking on the "Share analytics" `primaryButton`
        composeTestRule.waitForIdle()
        composeTestRule.onNode(primaryButton).performClick()
        composeTestRule.waitForIdle()
        // Then call optIn()
        assertEquals(true, actual)
        verifyBlocking(repository) { optIn() }
    }

    @Test
    fun doNotShareAnalytics() {
        // Given the OptOutScreen Composable
        var actual = false
        composeTestRule.setContent {
            OptInScreen(viewModel) { actual = true }
        }
        // When clicking on the "Do not share analytics" `textButton`
        composeTestRule.waitForIdle()
        composeTestRule.onNode(textButton).performClick()
        composeTestRule.waitForIdle()
        // Then call optOut()
        assertEquals(true, actual)
        verifyBlocking(repository) { optOut() }
    }
}
