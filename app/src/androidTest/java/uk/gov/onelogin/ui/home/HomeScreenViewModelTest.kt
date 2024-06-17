package uk.gov.onelogin.ui.home

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltAndroidTest
class HomeScreenViewModelTest : TestCase() {
    private val tokenRepository: TokenRepository = mock()
    private val saveToSecureStore: SaveToSecureStore = mock()
    private val saveTokenExpiry: SaveTokenExpiry = mock()
    private val getEmail: GetEmail = mock()

    private val viewModel = HomeScreenViewModel(
        tokenRepository,
        saveToSecureStore,
        saveTokenExpiry,
        getEmail
    )

    @Test
    fun saveTokenWhenTokensNotNull() {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "test",
            accessTokenExpirationTime = 1L,
            idToken = "id"
        )

        runBlocking {
            whenever(tokenRepository.getTokenResponse()).thenReturn(testResponse)

            viewModel.saveTokens(composeTestRule.activity as FragmentActivity)

            verify(saveToSecureStore).invoke(
                composeTestRule.activity as FragmentActivity,
                Keys.ACCESS_TOKEN_KEY,
                "test"
            )
            verify(saveToSecureStore).invoke(
                composeTestRule.activity as FragmentActivity,
                Keys.ID_TOKEN_KEY,
                "id"
            )
            verify(saveTokenExpiry).invoke(testResponse.accessTokenExpirationTime)
        }
    }

    @Test
    fun saveTokenWhenTokensNotNullIdTokenIsNull() {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "test",
            accessTokenExpirationTime = 1L
        )

        runBlocking {
            whenever(tokenRepository.getTokenResponse()).thenReturn(testResponse)

            viewModel.saveTokens(composeTestRule.activity as FragmentActivity)

            verify(saveToSecureStore).invoke(
                composeTestRule.activity as FragmentActivity,
                Keys.ACCESS_TOKEN_KEY,
                "test"
            )
            verify(saveToSecureStore, times(0)).invoke(any(), eq(Keys.ID_TOKEN_KEY), any())
            verify(saveTokenExpiry).invoke(testResponse.accessTokenExpirationTime)
        }
    }
}
