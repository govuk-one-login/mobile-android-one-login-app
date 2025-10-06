package uk.gov.onelogin.features.login.domain.signin.loginredirect

import android.content.Intent
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.AppIntegrityParameters
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.logging.api.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult

@Suppress("MaxLineLength")
class HandleLoginRedirectTest {
    private val mockAppIntegrity: AppIntegrity = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockLogger: Logger = mock()
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
    private val accessDeniedError = AuthenticationError(
        "access_denied",
        AuthenticationError.ErrorType.ACCESS_DENIED
    )
    private val oauthError = AuthenticationError(
        "oauth_error",
        AuthenticationError.ErrorType.OAUTH
    )
    private val tokenError = AuthenticationError(
        "token_error",
        AuthenticationError.ErrorType.OAUTH
    )

    private val exceptionNullMessage = Exception()

    private val handleLoginRedirect = HandleLoginRedirectImpl(
        mockAppIntegrity,
        mockLoginSession,
        mockLogger
    )

    @Test
    fun `handle() should call onSuccess with tokens when attestation and PoP are successful`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }

            // When
            handleLoginRedirect.handle(
                mockIntent,
                { },
                {
                    assertEquals(tokenResponse, it)
                }
            )
        }

    @Test
    fun `handle() should call onFailure when getProofOfPossession returns Failure with error`() =
        runTest {
            val expectedResult = SignedPoP.Failure("test", Error("error"))
            val expectedError = AppIntegrity.Companion
                .AppIntegrityProofOfPossessionException(expectedResult.error)
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(expectedResult)

            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertEquals("error", it?.message)
                },
                { }
            )

            verify(mockLogger).error(
                expectedError.javaClass.simpleName,
                expectedError.message ?: expectedResult.reason,
                expectedError
            )
            verifyNoInteractions(mockLoginSession)
        }

    @Test
    fun `handle() should call onFailure when getProofOfPossession returns Failure without error`() =
        runTest {
            val expectedResult = SignedPoP.Failure("test")
            val expectedError = AppIntegrity.Companion
                .AppIntegrityProofOfPossessionException(null)
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(testAttestation)
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(expectedResult)

            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertEquals(null, it?.message)
                },
                { }
            )

            verify(mockLogger).error(
                expectedError.javaClass.simpleName,
                expectedError.message ?: expectedResult.reason,
                expectedError
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
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                val appIntegrityParameters = it.arguments[1] as AppIntegrityParameters
                assertEquals("", appIntegrityParameters.attestation)
            }

            // When
            handleLoginRedirect.handle(
                mockIntent,
                { },
                {
                    assertEquals(tokenResponse, it)
                }
            )
        }

    @Test
    fun `onSuccess, savedAttestation is empty and attestation and PoP are successful`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn("")
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                val appIntegrityParameters = it.arguments[1] as AppIntegrityParameters
                assertEquals("", appIntegrityParameters.attestation)
            }

            // When
            handleLoginRedirect.handle(
                mockIntent,
                { },
                {
                    assertEquals(tokenResponse, it)
                }
            )
        }

    @Test
    fun `onFailure, savedAttestation is null and getClientAttestation returns Failure`() =
        runTest {
            val expectedError = AppIntegrity.Companion.ClientAttestationException("error")
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Failure("error"))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))

            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertEquals("error", it?.message)
                },
                { }
            )

            verifyNoInteractions(mockLoginSession)
        }

    @Test
    fun `onSuccess, when savedAttestation is null and getClientAttestation returns NotRequired`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired(testAttestation))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                val appIntegrityParameters = it.arguments[1] as AppIntegrityParameters
                assertEquals(testAttestation, appIntegrityParameters.attestation)
            }

            // When
            handleLoginRedirect.handle(
                mockIntent,
                { },
                {
                    assertEquals(tokenResponse, it)
                }
            )
        }

    @Test
    fun `onFailure, handleLoginFinalise returns access_denied failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (error: AuthenticationError) -> Unit).invoke(accessDeniedError)
            }

            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    it?.let {
                        verify(mockLogger).error(
                            it::class.java.simpleName,
                            it.message ?: "No message",
                            it
                        )
                    }
                    assertEquals("access_denied", it?.message)
                },
                { }
            )
        }

    @Test
    fun `onFailure, handleLoginFinalise returns oauth_error failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (error: AuthenticationError) -> Unit).invoke(oauthError)
            }
            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertEquals("oauth_error", it?.message)
                },
                { }
            )
        }

    @Test
    fun `onFailure, handleLoginFinalise returns token_error failure callback`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (error: AuthenticationError) -> Unit).invoke(tokenError)
            }
            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertEquals("token_error", it?.message)
                },
                { }
            )
        }

    @Test
    fun `onFailure, handleLoginFinalise returns error with null message`() =
        runTest {
            whenever(mockAppIntegrity.retrieveSavedClientAttestation()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success(""))
            whenever(mockAppIntegrity.getProofOfPossession()).thenReturn(SignedPoP.Success(testJwt))
            whenever(mockLoginSession.finalise(any(), any(), any(), any())).thenAnswer {
                @Suppress("unchecked_cast")
                (it.arguments[3] as (error: Throwable) -> Unit).invoke(exceptionNullMessage)
            }
            // When
            handleLoginRedirect.handle(
                mockIntent,
                {
                    assertNull(it?.message)
                },
                { }
            )
        }
}
