package uk.gov.onelogin.features.unit.network.provider

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.io.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.AuthenticationResponse
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.android.network.service.NetworkService
import uk.gov.logging.api.v3.LogLevel
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.isLogLevel
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.network.provider.StsAuthenticationProvider
import uk.gov.onelogin.features.network.provider.StsAuthenticationProvider.Companion.AUTHENTICATION_DENIED
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST")
class StsAuthenticationProviderTest {
    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockActivityProvider: ActivityProvider = mock()
    private val mockRefreshExchange: RefreshExchange = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockIsAccessTokenExpired: IsTokenExpired = mock()
    private val mockNavigator: Navigator = mock()
    private val logger = MemorisedLogger()
    private val stubHttpClient = StubHttpClient()
    private val networkService: NetworkService = DefaultNetworkService(stubHttpClient)

    private lateinit var provider: AuthenticationProvider

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        whenever(mockTokenRepository.getTokenResponse()).thenReturn(loginTokens)
        whenever(mockActivityProvider.getCurrentActivity()).thenReturn(mockFragmentActivity)
        wheneverBlocking { mockIsAccessTokenExpired.invoke() }.thenReturn(false)
        wheneverBlocking { mockRefreshExchange.getTokens(any(), any()) }
            .thenAnswer {
                (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                    .invoke(RefreshExchangeResult.Success)
            }

        stubHttpClient.response = GenericHttpResponse(200, tokenResponseJson)

        provider =
            StsAuthenticationProvider(
                mockActivityProvider,
                "url",
                mockTokenRepository,
                mockIsAccessTokenExpired,
                networkService,
                mockNavigator,
                mockRefreshExchange,
                mockSignOutUseCase,
                logger
            )
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `access token expired, refresh exchanged has failed with re-auth required`() =
        runTest {
            wheneverBlocking { mockIsAccessTokenExpired.invoke() }.thenReturn(true)

            wheneverBlocking { mockRefreshExchange.getTokens(any(), any()) }
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.ReauthRequired)
                }

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.REFRESH_EXCHANGE_ERROR_MSG,
                response.error.message
            )
            verify(mockNavigator).navigate(SignOutRoutes.ReAuth)
        }

    @Test
    fun `access token expired, refresh exchanged has failed with client attestation failure`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.ClientAttestationFailure)
                }

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.REFRESH_EXCHANGE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with user cancelled`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.UserCancelledBioPrompt)
                }
            stubHttpClient.exception = IOException()

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.SERVICE_TOKEN_FAILURE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with sign in required`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.FirstTimeUser)
                }

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.MANUAL_SIGN_IN_REQUIRED_ERROR_MSG,
                response.error.message
            )
            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.ReAuthError)
        }

    @Test
    fun `access token expired, refresh exchanged has failed with offline network`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (RefreshExchangeResult) -> Unit)
                        .invoke(RefreshExchangeResult.OfflineNetwork)
                }
            stubHttpClient.exception = IOException()

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.SERVICE_TOKEN_FAILURE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `access token expired, refresh exchanged has failed with success`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            stubHttpClient.response = GenericHttpResponse(200, tokenResponseJson)

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Success>(response)
            assertEquals(BEARER_TOKEN, response.bearerToken)
        }

    @Test
    fun `access token expired, activity fragment is null`() =
        runTest {
            whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
            whenever(mockActivityProvider.getCurrentActivity()).thenReturn(null)

            val response = provider.fetchBearerToken(SCOPE)

            val error = StsAuthenticationProvider.Companion.FragmentActivityNull()
            assertThat(logger, hasItem(allOf(isLogLevel(LogLevel.Error), hasMessage(error.msg))))
            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.REFRESH_EXCHANGE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `original exception when API call fails`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(500, "error"),
                IllegalStateException("error")
            )

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals("API responded with 500", response.error.message)
        }

    @Test
    fun `SERVICE_TOKEN_FAILURE_ERROR_MSG when transport failure`() =
        runTest {
            stubHttpClient.exception = IOException()

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.SERVICE_TOKEN_FAILURE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `api response is success but json decode fails, failure returned`() =
        runTest {
            stubHttpClient.response = GenericHttpResponse(200, "hello")

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
        }

    @Test
    fun `access token only, api response is failure with 400 with no error message`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AUTHENTICATION_DENIED, ""),
                IllegalStateException()
            )
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                LoginTokens(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            verify(mockNavigator).navigate(SignOutRoutes.ReAuth)
            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                "API responded with $AUTHENTICATION_DENIED",
                response.error.message
            )
        }

    @Test
    fun `access token only, api response is failure with 400 with error message`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AUTHENTICATION_DENIED, "denied"),
                IllegalStateException("denied")
            )
            whenever(mockTokenRepository.getTokenResponse()).thenReturn(
                LoginTokens(
                    tokenType = "type",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 1L,
                    idToken = "idToken"
                )
            )

            val response = provider.fetchBearerToken("scope")

            verify(mockNavigator).navigate(SignOutRoutes.ReAuth)
            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                "API responded with $AUTHENTICATION_DENIED",
                response.error.message
            )
        }

    @Test
    fun `access token only, transport failure`() =
        runTest {
            stubHttpClient.exception = IOException()

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Failure>(response)
            assertEquals(
                StsAuthenticationProvider.Companion.SERVICE_TOKEN_FAILURE_ERROR_MSG,
                response.error.message
            )
        }

    @Test
    fun `api response is success, success returned`() =
        runTest {
            stubHttpClient.response = GenericHttpResponse(200, tokenResponseJson)

            val response = provider.fetchBearerToken(SCOPE)

            assertInstanceOf<AuthenticationResponse.Success>(response)
            assertEquals(BEARER_TOKEN, response.bearerToken)
        }

    companion object {
        private val loginTokens =
            LoginTokens(
                tokenType = "type",
                accessToken = "accessToken",
                accessTokenExpirationTime = 1L,
                idToken = "idToken"
            )
        private const val SCOPE = "scope"
        private const val TOKEN_TYPE = "Bearer"
        private const val BEARER_TOKEN = "BearerToken"
        private val tokenResponseJson =
            """
            {
            "access_token": "$BEARER_TOKEN",
            "expires_in": 180,
            "token_type": "$TOKEN_TYPE"
            }
            """.trimIndent()
    }
}
