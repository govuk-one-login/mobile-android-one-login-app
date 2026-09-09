package uk.gov.onelogin.features.unit.login.domain.signin.remotelogin.finalise

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.logging.api.v3.LogLevel
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.isLogLevel
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLoginImpl
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@Suppress("MaxLineLength")
class FinaliseRemoteLoginTest {
    private lateinit var context: Context
    private val mockAppIntegrity: AppIntegrity = mock()
    private val mockLoginSession: LoginSession = mock()
    private val logger = MemorisedLogger()
    private val mockIntent: Intent = mock()

    private val testJwt = "testJwt"
    private val testAttestation = "testAttestation"
    private val testAccessToken = "testAccessToken"
    private var testIdToken: String = "testIdToken"
    private val tokenResponse =
        TokenResponse(
            "testType",
            testAccessToken,
            1L,
            testIdToken,
            "testRefreshToken"
        )
    private val accessDeniedError =
        AuthenticationError(
            "access_denied",
            AuthenticationError.ErrorType.ACCESS_DENIED
        )
    private val oauthError =
        AuthenticationError(
            "oauth_error",
            AuthenticationError.ErrorType.OAUTH
        )
    private val tokenError =
        AuthenticationError(
            "token_error",
            AuthenticationError.ErrorType.OAUTH
        )

    private val exceptionNullMessage = Exception()

    private lateinit var finaliseRemoteLogin: FinaliseRemoteLogin

    @BeforeEach
    fun setup() {
        context = mock()
        whenever(context.getString(any(), anyVararg()))
            .thenReturn("https://token.account.gov.uk")
        finaliseRemoteLogin =
            FinaliseRemoteLoginImpl(
                context,
                mockAppIntegrity,
                mockLoginSession,
                logger
            )
    }

    @Test
    fun `handle() should call onSuccess with tokens when attestation and PoP are successful`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Success>(result)
            assertEquals(tokenResponse, result.tokenResponse)
        }

    @Test
    fun `handle() should call onFailure when getProofOfPossession returns Failure with error`() =
        runTest {
            val expectedResult = SignedPoP.Failure("test", Error("error"))
            val expectedError =
                AppIntegrityException.ProofOfPossessionException(expectedResult.error!!)
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(expectedResult)

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertEquals("error", result.error.message)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(
                            expectedError.message ?: expectedResult.reason
                        )
                    )
                )
            )
            verifyNoInteractions(mockLoginSession)
        }

    @Test
    fun `handle() should call onFailure when getProofOfPossession returns Failure without error`() =
        runTest {
            val expectedResult = SignedPoP.Failure("test")
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(expectedResult)

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(containsString(expectedResult.reason))
                    )
                )
            )
            verifyNoInteractions(mockLoginSession)
        }

    @Test
    fun `onSuccess, savedAttestation is null and attestation and PoP are successful`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Success>(result)
            assertEquals(tokenResponse, result.tokenResponse)
        }

    @Test
    fun `onSuccess, savedAttestation is empty and attestation and PoP are successful`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn("")
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Success>(result)
            assertEquals(tokenResponse, result.tokenResponse)
        }

    @Test
    fun `onFailure, savedAttestation is null and getClientAttestation returns Failure`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(
                    AttestationResult.Failure(
                        type = AppIntegrityException.AppIntegrityErrorType.GENERIC,
                        error = Exception("error")
                    )
                )

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertEquals("error", result.error.message)
            verifyNoInteractions(mockLoginSession)
        }

    @Test
    fun `onSuccess, when savedAttestation is null and getClientAttestation returns NotRequired`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired(testAttestation))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Success>(result)
            assertEquals(tokenResponse, result.tokenResponse)
        }

    @Test
    fun `onFailure, handleLoginFinalise returns access_denied failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[4] as (error: AuthenticationError) -> Unit).invoke(accessDeniedError)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertEquals("access_denied", result.error.message)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(result.error.message!!)
                    )
                )
            )
        }

    @Test
    fun `onFailure, handleLoginFinalise returns oauth_error failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[4] as (error: AuthenticationError) -> Unit).invoke(oauthError)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertEquals("oauth_error", result.error.message)
        }

    @Test
    fun `onFailure, handleLoginFinalise returns token_error failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[4] as (error: AuthenticationError) -> Unit).invoke(tokenError)
            }
            // When
            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertEquals("token_error", result.error.message)
        }

    @Test
    fun `onFailure, handleLoginFinalise returns error with null message`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[4] as (error: Throwable) -> Unit).invoke(exceptionNullMessage)
            }

            val result = finaliseRemoteLogin.handle(mockIntent)

            assertIs<FinaliseRemoteLogin.Result.Failure>(result)
            assertNull(result.error.message)
        }
}

