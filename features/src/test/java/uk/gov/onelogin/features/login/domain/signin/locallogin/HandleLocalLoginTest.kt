package uk.gov.onelogin.features.login.domain.signin.locallogin

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.MockitoHelper
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("LargeClass")
class HandleLocalLoginTest {
    private lateinit var mockActivity: FragmentActivity
    private lateinit var mockGetRefreshTokenExpiry: GetTokenExpiry
    private lateinit var mockGetAccessTokenExpiry: GetTokenExpiry
    private lateinit var mockTokenRepository: TokenRepository
    private lateinit var mockGetFromEncryptedSecureStore: GetFromEncryptedSecureStore
    private lateinit var mockBioPrefHandler: LocalAuthManager
    private lateinit var mockIsAccessTokenExpired: IsTokenExpired
    private lateinit var mockIsRefreshTokenExpired: IsTokenExpired
    private lateinit var mockGetPersistentId: GetPersistentId

    private lateinit var useCase: HandleLocalLogin

    @BeforeEach
    fun setup() {
        mockActivity = mock()
        mockGetRefreshTokenExpiry = mock()
        mockGetAccessTokenExpiry = mock()
        mockTokenRepository = mock()
        mockGetFromEncryptedSecureStore = mock()
        mockBioPrefHandler = mock()
        mockIsAccessTokenExpired = mock()
        mockIsRefreshTokenExpired = mock()
        mockGetPersistentId = mock()
        useCase =
            HandleLocalLoginImpl(
                mockGetAccessTokenExpiry,
                mockGetRefreshTokenExpiry,
                mockTokenRepository,
                mockIsAccessTokenExpired,
                mockIsRefreshTokenExpired,
                mockGetFromEncryptedSecureStore,
                mockBioPrefHandler,
                mockGetPersistentId
            )
    }

    @Test
    fun `access token expired and not null requires re-auth`() =
        runTest {
            mockGetPersistentSessionIdSuccessful()
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(1)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `refresh token expired and not null requires re-auth`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `access token expired and null requires sign-in`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(null)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `refresh token null, fallback to access token only flow`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            val expected =
                LocalAuthStatus.Success(
                    payload =
                        mapOf(
                            AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
                        )
                )
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(expected)
            }

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(expected, actual)
        }

    @Test
    fun `refresh and access token null, fallback to access token only flow, re-auth required`() =
        runTest {
            val encryptedStoreResult =
                LocalAuthStatus.Success(
                    payload =
                        mapOf(
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
                        )
                )
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            val expected = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(encryptedStoreResult)
            }

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(expected, actual)
        }

    @Test
    fun `refresh token null, access token valid, fallback to access token only flow`() =
        runTest {
            val expected =
                LocalAuthStatus.Success(
                    payload =
                        mapOf(
                            AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
                        )
                )
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(expected)
            }

            useCase(
                mockActivity
            ) {
                actual = it
            }

            assertEquals(expected, actual)
        }

    @Test
    fun `refresh token not issued, id token null, access token valid, re-auth required`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    ArgumentMatchers.contains(AuthTokenStoreKeys.ID_TOKEN_KEY),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(
                        payload =
                            mapOf(
                                AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken"
                            )
                    )
                )
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `id token null, refresh token valid, re-auth required`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(
                        payload =
                            mapOf(
                                AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "refreshToken"
                            )
                    )
                )
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `access token null, refresh token valid, re-auth required`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(
                        payload =
                            mapOf(
                                AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken",
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "refreshToken"
                            )
                    )
                )
            }

            useCase(mockActivity) {
                actual = it
                println(it)
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `refresh token issues BUT null, re-auth required`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))
            wheneverBlocking {
                mockGetFromEncryptedSecureStore.invoke(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            }.thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(
                        payload =
                            mapOf(
                                AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                                AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
                            )
                    )
                )
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `refresh token not issued, no biometrics and access token valid but expiry is null`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry()).thenReturn(null)
            whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `refresh token not issued, no biometrics and access token expiry is valid`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry()).thenReturn(1)
            whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `refresh token valid, no biometrics and access token valid but null`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry()).thenReturn(null)
            whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `refresh token valid, no biometrics and access token expiry valid`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
        }

    @Test
    fun `access token retrieval from encrypted secure store returns FirstTimeUser`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.FirstTimeUser)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `refresh token retrieval from encrypted secure store returns FirstTimeUser`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.FirstTimeUser)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `access token retrieval from encrypted secure store returns ReauthRequired`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ReauthRequired)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `refresh token retrieval from encrypted secure store returns ReauthRequired`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.FirstTimeUser
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(unexpiredTime)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ReauthRequired)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.ReauthRequired, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `access token retrieval from encrypted secure store returns UnrecoverableError`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UnrecoverableError)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.UnrecoverableError, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `refresh token retrieval from encrypted secure store returns UnrecoverableError`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UnrecoverableError)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.UnrecoverableError, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `access token retrieval from encrypted secure store returns UserCancelledBioPrompt`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UserCancelledBioPrompt)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.UserCancelledBioPrompt, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `refresh token retrieval from encrypted secure store returns UserCancelledBioPrompt`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UserCancelledBioPrompt)
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.UserCancelledBioPrompt, actual)
            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }

    @Test
    fun `verify persistent session ID is null returns FirstTimeUser`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            whenever(mockGetPersistentId.invoke()).thenReturn(null)
            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `verify persistent session ID is empty returns FirstTimeUser`() =
        runTest {
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            whenever(mockGetPersistentId.invoke()).thenReturn("")
            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.FirstTimeUser, actual)
        }

    @Test
    fun `access token only flow successfully completed`() =
        runTest {
            val accessToken = "Token"
            val idToken = "IdToken"
            val tokenResponse =
                mapOf(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY to accessToken,
                    AuthTokenStoreKeys.ID_TOKEN_KEY to idToken
                )
            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetAccessTokenExpiry()).thenReturn(unexpiredTime)
            whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(tokenResponse)
                )
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.Success(tokenResponse), actual)
            verify(mockTokenRepository).setTokenResponse(
                LoginTokens(
                    accessToken = accessToken,
                    idToken = idToken,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
        }

    @Test
    fun `refresh token flow successfully completed`() =
        runTest {
            val accessToken = "Token"
            val idToken = "IdToken"
            val refreshToken = "RefreshToken"
            val tokenResponse =
                mapOf(
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY to accessToken,
                    AuthTokenStoreKeys.ID_TOKEN_KEY to idToken,
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY to refreshToken
                )

            var actual: LocalAuthStatus = LocalAuthStatus.ReauthRequired
            mockGetPersistentSessionIdSuccessful()
            whenever(mockGetAccessTokenExpiry()).thenReturn(unexpiredTime)
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
            whenever(mockBioPrefHandler.localAuthPreference)
                .thenReturn(LocalAuthPreference.Enabled(false))

            whenever(
                mockGetFromEncryptedSecureStore(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(tokenResponse)
                )
            }

            useCase(mockActivity) {
                actual = it
            }

            assertEquals(LocalAuthStatus.Success(tokenResponse), actual)
            verify(mockTokenRepository).setTokenResponse(
                LoginTokens(
                    accessToken = accessToken,
                    idToken = idToken,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
        }

    private suspend fun mockGetPersistentSessionIdSuccessful() {
        whenever(mockGetPersistentId.invoke()).thenReturn("persistentId")
    }

    companion object {
        val unexpiredTime =
            Instant
                .now()
                .plus(1, ChronoUnit.HOURS)
                .toEpochMilli()
    }
}
