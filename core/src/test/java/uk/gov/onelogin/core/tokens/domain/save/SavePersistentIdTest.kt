package uk.gov.onelogin.core.tokens.domain.save

import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class SavePersistentIdTest {
    private lateinit var savePersistentId: SavePersistentId
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveToOpenSecureStore: SaveToOpenSecureStore = mock()
    private val logger = SystemLogger()

    // encoded ID token with persistent ID in the body
    private val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwZXJzaXN0ZW50X2lkIjoiMTIzNCJ9"

    @BeforeEach
    fun setup() {
        savePersistentId =
            SavePersistentIdImpl(
                mockTokenRepository,
                mockSaveToOpenSecureStore,
                logger
            )
    }

    @Test
    fun savePersistentIdWhenTokensNotNull() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = idToken
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            savePersistentId()

            runBlocking {
                verify(mockSaveToOpenSecureStore).save(
                    AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                    "1234"
                )
            }
        }

    @Test
    fun notSaveIdWhenTokensNotNullMissingPersistentId() =
        runTest {
            val testResponse =
                TokenResponse(
                    tokenType = "test",
                    accessToken = "access",
                    accessTokenExpirationTime = 1L,
                    idToken = "id"
                )

            whenever(mockTokenRepository.getTokenResponse()).thenReturn(testResponse)

            savePersistentId()
            runBlocking {
                verifyNoInteractions(mockSaveToOpenSecureStore)
            }
            assertTrue(logger.size == 1)
        }

    @Test
    fun noSaveIdWhenTokensIsNull() {
        runBlocking {
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(null)

            savePersistentId()

            verifyNoInteractions(mockSaveToOpenSecureStore)
            assert(logger.contains("Failed to save Persistent ID, tokens not available"))
        }
    }
}
