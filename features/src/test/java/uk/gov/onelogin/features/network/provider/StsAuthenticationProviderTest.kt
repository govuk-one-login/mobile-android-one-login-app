package uk.gov.onelogin.features.network.provider

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.AuthenticationResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertEquals

class StsAuthenticationProviderTest {
    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockActivityProvider: ActivityProvider = mock()
    private val mockRefreshExchange: RefreshExchange = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockIsAccessTokenExpired: IsTokenExpired = mock()
    private val mockNavigator: Navigator = mock()
    private val logger = SystemLogger()
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var provider: AuthenticationProvider

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `access token expired, refresh exchanged has failed with re-auth required`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.ReAuthRequired)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                (response as AuthenticationResponse.Failure).error.message
            )
            verify(mockNavigator).navigate(SignOutRoutes.Info)
        }

    @Test
    fun `access token expired, refresh exchanged has failed with client attestation failure`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.ClientAttestationFailure)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with user cancelled`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.UserCancelledBioPrompt)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Failure",
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                "Failed to fetch service token",
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with sign in required`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.SignInRequired)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                StsAuthenticationProvider.MANUAL_SIGN_IN_REQUIRED_ERROR_MSG,
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                StsAuthenticationProvider.MANUAL_SIGN_IN_REQUIRED_ERROR_MSG,
                (response as AuthenticationResponse.Failure).error.message
            )
            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.ReAuthError)
        }

    @Test
    fun `access token expired, refresh exchanged has failed with bio check failed`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.BioCheckFailed)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Failure",
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                "Failed to fetch service token",
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with offline network`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.OfflineNetwork)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Failure",
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                "Failed to fetch service token",
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with success`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(mockFragmentActivity)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.Success)
                }

            setupProvider(
                ApiResponse.Success(
                    "{\n" +
                        "    \"access_token\": \"token\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 180\n" +
                        "}"
                )
            )
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Success",
                response is AuthenticationResponse.Success
            )
            assertEquals(
                "token",
                (response as AuthenticationResponse.Success).bearerToken
            )
        }

    @Test
    fun `access token expired, activity fragment is null`() =
        runTest {
            setupProvider(ApiResponse.Loading, true)
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )
            whenever(mockActivityProvider.getCurrentActivity())
                .thenReturn(null)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.Success)
                }

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                StsAuthenticationProvider.REFRESH_EXCHANGE_ERROR_MSG,
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `api response is not success, failure returned`() =
        runTest {
            setupProvider(ApiResponse.Failure(500, Exception("error")))
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Failure",
                response is AuthenticationResponse.Failure
            )
            assertEquals(
                "Failed to fetch service token",
                (response as AuthenticationResponse.Failure).error.message
            )
        }

    @Test
    fun `api response is success but json decode fails, failure returned`() =
        runTest {
            setupProvider(ApiResponse.Success("hello"))
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Failure",
                response is AuthenticationResponse.Failure
            )
            MatcherAssert.assertThat("logger has a log", logger.size == 1)
        }

    @Test
    fun `api response is success, success returned`() =
        runTest {
            setupProvider(
                ApiResponse.Success(
                    "{\n" +
                        "    \"access_token\": \"token\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 180\n" +
                        "}"
                )
            )
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                TokenResponse(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            MatcherAssert.assertThat(
                "response is Success",
                response is AuthenticationResponse.Success
            )
            assertEquals(
                "token",
                (response as AuthenticationResponse.Success).bearerToken
            )
        }

    private suspend fun setupProvider(
        httpResponse: ApiResponse,
        isAccessTokenExpired: Boolean = false
    ) {
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(isAccessTokenExpired)
        stubHttpClient = StubHttpClient(httpResponse)
        provider =
            StsAuthenticationProvider(
                mockActivityProvider,
                "url",
                mockTokenRepository,
                mockIsAccessTokenExpired,
                stubHttpClient,
                mockNavigator,
                mockRefreshExchange,
                mockSignOutUseCase,
                logger
            )
    }
}
