package uk.gov.onelogin.login.usecase

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore

class SaveTokensTest {
    private val saveTokens by lazy {
        SaveTokensImpl(
            mockTokenRepository,
            mockSaveToSecureStore,
            mockSaveToOpenSecureStore,
            mockGetPersistentId
        )
    }
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveToSecureStore: SaveToSecureStore = mock()
    private val mockSaveToOpenSecureStore: SaveToOpenSecureStore = mock()
    private val mockGetPersistentId: GetPersistentId = mock()

    @Test
    fun saveTokenWhenTokensNotNull() = runTest {
        val testResponse = TokenResponse(
            tokenType = "test",
            accessToken = "access",
            accessTokenExpirationTime = 1L,
            idToken = "id"
        )

        whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)
        whenever(mockGetPersistentId.invoke()).thenReturn("persistentId")

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
            verify(mockSaveToOpenSecureStore).invoke(
                Keys.PERSISTENT_ID_KEY,
                "persistentId"
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
        whenever(mockGetPersistentId.invoke()).thenReturn(null)

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
            verifyNoInteractions(mockGetPersistentId)
        }
    }
}
