package uk.gov.onelogin.ui.home

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltAndroidTest
class HomeScreenViewModelTest : TestCase() {
    private val tokenRepository: TokenRepository = mock()
    private val saveToSecureStore: SaveToSecureStore = mock()
    private val saveTokenExpiry: SaveTokenExpiry = mock()

    private val viewModel = HomeScreenViewModel(
        tokenRepository,
        saveToSecureStore,
        saveTokenExpiry
    )

    @Test
    fun saveTokenWhenTokensNotNull() {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "test",
            accessTokenExpirationTime = 1L
        )

        whenever(tokenRepository.getTokenResponse()).thenReturn(testResponse)

        viewModel.saveTokens(composeTestRule.activity as FragmentActivity)

        runBlocking {
            verify(saveTokenExpiry).invoke(testResponse.accessTokenExpirationTime)
            verify(saveToSecureStore).invoke(
                composeTestRule.activity,
                Keys.ACCESS_TOKENS_KEY,
                testResponse.accessToken
            )
        }
    }
}
