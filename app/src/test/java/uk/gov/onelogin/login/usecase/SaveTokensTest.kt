package uk.gov.onelogin.login.usecase

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore

class SaveTokensTest {
    private lateinit var saveTokens: SaveTokens
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveToSecureStore: SaveToSecureStore = mock()
    private val mockSaveToOpenSecureStore: SaveToOpenSecureStore = mock()

    // encoded ID token with persistent ID in the body
    private val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwZXJzaXN0ZW50X2lkIjoiMTIzNCJ9"

    @BeforeEach
    fun setup() {
        saveTokens = SaveTokensImpl(
            mockTokenRepository,
            mockSaveToSecureStore,
            mockSaveToOpenSecureStore
        )
    }

    @Test
    fun saveTokenWhenTokensNotNull() = runTest {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "access",
            accessTokenExpirationTime = 1L,
            idToken = idToken
        )

        whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

        saveTokens()

        runBlocking {
            verify(mockSaveToSecureStore).invoke(
                Keys.ACCESS_TOKEN_KEY,
                "access"
            )
            verify(mockSaveToSecureStore).invoke(
                Keys.ID_TOKEN_KEY,
                idToken
            )
            verify(mockSaveToOpenSecureStore).invoke(
                Keys.PERSISTENT_ID_KEY,
                "1234"
            )
        }
    }

    @Test
    fun saveTokenWhenTokensNotNullMissingPersistentId() = runTest {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "access",
            accessTokenExpirationTime = 1L,
            idToken = "id"
        )

        whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

        saveTokens()
        runBlocking {
            verify(mockSaveToSecureStore).invoke(
                Keys.ACCESS_TOKEN_KEY,
                "access"
            )
            verify(mockSaveToSecureStore).invoke(
                Keys.ID_TOKEN_KEY,
                "id"
            )
            verify(mockSaveToOpenSecureStore, times(0)).invoke(
                Keys.PERSISTENT_ID_KEY,
                "1234"
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
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            saveTokens()

            verify(mockSaveToSecureStore).invoke(
                Keys.ACCESS_TOKEN_KEY,
                "test"
            )
            verify(mockSaveToSecureStore, times(0)).invoke(any(), eq(Keys.ID_TOKEN_KEY))
            verify(mockSaveToOpenSecureStore, times(0)).invoke(any(), any())
        }
    }

    @Test
    fun noSaveTokenWhenTokensIsNull() {
        runBlocking {
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)

            saveTokens()

            verifyNoInteractions(mockSaveToSecureStore)
            verifyNoInteractions(mockSaveToOpenSecureStore)
        }
    }
}
