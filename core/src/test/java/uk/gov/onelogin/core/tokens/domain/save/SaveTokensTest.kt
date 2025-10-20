package uk.gov.onelogin.core.tokens.domain.save

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class SaveTokensTest {
    private lateinit var saveTokens: SaveTokens
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveToTokenSecureStore: SaveToTokenSecureStore = mock()

    // encoded ID token with persistent ID in the body
    private val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwZXJzaXN0ZW50X2lkIjoiMTIzNCJ9"

    @BeforeEach
    fun setup() {
        saveTokens =
            SaveTokensImpl(
                mockTokenRepository,
                mockSaveToTokenSecureStore
            )
    }

    @Test
    fun saveTokenWhenTokensNotNullWithRefresh() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = idToken,
                    refreshToken = "refresh"
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            saveTokens.save(testResponse.refreshToken)

            runBlocking {
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    "access"
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                    idToken
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY,
                    "refresh"
                )
            }
        }

    @Test
    fun saveTokenWhenTokensNotNull() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = idToken
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            saveTokens.save(testResponse.refreshToken)

            runBlocking {
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    "access"
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                    idToken
                )
            }
        }

    @Test
    fun saveTokenWhenTokensNotNullMissingPersistentId() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = "id"
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            saveTokens.save(testResponse.refreshToken)
            runBlocking {
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    "access"
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                    "id"
                )
            }
        }

    @Test
    fun saveTokenWhenTokensNotNullMissingPersistentIdWithRefresh() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = "id",
                    refreshToken = "refresh"
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            saveTokens.save(testResponse.refreshToken)
            runBlocking {
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    "access"
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                    "id"
                )
                verify(mockSaveToTokenSecureStore).invoke(
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY,
                    "refresh"
                )
            }
        }

    @Test
    fun noSaveTokenWhenTokensIsNull() {
        runBlocking {
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)

            saveTokens.save(null)

            verifyNoInteractions(mockSaveToTokenSecureStore)
        }
    }
}
