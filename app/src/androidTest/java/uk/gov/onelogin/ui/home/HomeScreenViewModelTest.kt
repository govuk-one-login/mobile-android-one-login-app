package uk.gov.onelogin.ui.home

import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltAndroidTest
class HomeScreenViewModelTest : TestCase() {
    private val tokenRepository: TokenRepository = mock()
    private val saveToSecureStore: SaveToSecureStore = mock()
    private val saveToOpenSecureStore: SaveToOpenSecureStore = mock()
    private val saveTokenExpiry: SaveTokenExpiry = mock()
    private val getEmail: GetEmail = mock()
    private val getPersistentId: GetPersistentId = mock()

    private val viewModel by lazy {
        HomeScreenViewModel(
            tokenRepository,
            saveToSecureStore,
            saveToOpenSecureStore,
            saveTokenExpiry,
            getPersistentId,
            getEmail
        )
    }

    @Test
    fun saveTokenWhenTokensNotNull() {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "access",
            accessTokenExpirationTime = 1L,
            idToken = "id"
        )

        runBlocking {
            whenever(tokenRepository.getTokenResponse()).thenReturn(testResponse)
            whenever(getPersistentId.invoke()).thenReturn("persistentId")

            viewModel.saveTokens()

            verify(saveToSecureStore).invoke(
                Keys.ACCESS_TOKEN_KEY,
                "access"
            )
            verify(saveToSecureStore).invoke(
                Keys.ID_TOKEN_KEY,
                "id"
            )
            verify(saveTokenExpiry).invoke(testResponse.accessTokenExpirationTime)
            verify(saveToOpenSecureStore).invoke(
                Keys.PERSISTENT_ID_KEY,
                "persistentId"
            )
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

            viewModel.saveTokens()

            verify(saveToSecureStore).invoke(
                Keys.ACCESS_TOKEN_KEY,
                "test"
            )
            verify(saveToSecureStore, times(0)).invoke(any(), eq(Keys.ID_TOKEN_KEY))
            verify(saveToOpenSecureStore, times(0)).invoke(any(), any())
            verify(saveTokenExpiry).invoke(testResponse.accessTokenExpirationTime)
        }
    }

    @Test
    fun noSaveTokenWhenTokensIsNull() {
        runBlocking {
            whenever(tokenRepository.getTokenResponse()).thenReturn(null)

            viewModel.saveTokens()

            verify(saveToSecureStore, times(0)).invoke(any(), any())
            verify(saveToSecureStore, times(0)).invoke(any(), any())
            verify(saveToOpenSecureStore, times(0)).invoke(any(), any())
            verify(saveTokenExpiry, times(0)).invoke(any())
        }
    }

    @Test
    fun emailIsReturned() {
        whenever(getEmail()).thenReturn("test")

        assertEquals("test", viewModel.email)
    }

    @Test
    fun emailIsEmptyWhenNoEmailReturned() {
        whenever(getEmail()).thenReturn(null)

        assertEquals("", viewModel.email)
    }
}
