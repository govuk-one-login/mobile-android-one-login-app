package uk.gov.onelogin.features.unit.login.domain.signin.remotelogin.start

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.LoginException
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.utils.LocaleUtils
import uk.gov.onelogin.core.utils.UriParser
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLoginImpl
import kotlin.test.assertEquals

class StartRemoteLoginTest {
    private val mockContext: Context = mock()
    private val mockGetPersistentId: GetPersistentId = mock()
    private val mockLocaleUtils: LocaleUtils = mock()
    private val mockLoginSession: LoginSession = mock()
    private val mockAppIntegrity: AppIntegrity = mock()
    private val mockUriParser: UriParser = mock()
    private val mockLogger: Logger = mock()
    private val mockLauncher: ActivityResultLauncher<Intent> = mock()
    private val mockUri: Uri = mock()
    private val testAttestation = "testAttestation"
    private val testPersistentId = "12345"

    private lateinit var startRemoteLogin: StartRemoteLogin

    @BeforeEach
    fun setUp() {
        startRemoteLogin =
            StartRemoteLoginImpl(
                mockContext,
                mockLocaleUtils,
                mockLoginSession,
                mockGetPersistentId,
                mockAppIntegrity,
                mockUriParser,
                mockLogger
            )

        whenever(mockContext.getString(any())).thenReturn("")
        whenever(mockContext.getString(any(), any<String>())).thenReturn("")
        whenever(mockLocaleUtils.getLocaleAsSessionConfig()).thenReturn(
            LoginSessionConfiguration.Locale.EN
        )
        whenever(mockUriParser.parse(any())).thenReturn(mockUri)
    }

    @Test
    fun `login() - success with persistentId`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn(testPersistentId)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )

            startRemoteLogin.login(mockLauncher) {}

            argumentCaptor<LoginSessionConfiguration> {
                verify(mockLoginSession).present(eq(mockLauncher), capture())
                val capturedConfiguration = firstValue
                assertEquals(testPersistentId, capturedConfiguration.persistentSessionId)
                Assertions.assertEquals(
                    LoginSessionConfiguration.Locale.EN,
                    capturedConfiguration.locale
                )
            }
        }

    @Test
    fun `login() - success without persistentId`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn(null)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )

            startRemoteLogin.login(mockLauncher) {}

            argumentCaptor<LoginSessionConfiguration> {
                verify(mockLoginSession).present(eq(mockLauncher), capture())
                val capturedConfiguration = firstValue
                assertEquals(null, capturedConfiguration.persistentSessionId)
                Assertions.assertEquals(
                    LoginSessionConfiguration.Locale.EN,
                    capturedConfiguration.locale
                )
            }
        }

    @Test
    fun `login() - success with empty persistentId`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn("")
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Success(testAttestation)
            )

            startRemoteLogin.login(mockLauncher) {}

            argumentCaptor<LoginSessionConfiguration> {
                verify(mockLoginSession).present(eq(mockLauncher), capture())
                val capturedConfiguration = firstValue
                assertEquals(null, capturedConfiguration.persistentSessionId)
                Assertions.assertEquals(
                    LoginSessionConfiguration.Locale.EN,
                    capturedConfiguration.locale
                )
            }
        }

    @Test
    fun `login() - handles AttestationResult Failure`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn(testPersistentId)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.Failure(Exception("error"))
            )
            var checkFailed = false

            startRemoteLogin.login(mockLauncher) {
                checkFailed = true
            }

            assert(checkFailed)
        }

    @Test
    fun `login() - handles AttestationResult NotRequired with savedAttestation`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn(testPersistentId)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.NotRequired(testAttestation)
            )

            startRemoteLogin.login(mockLauncher) {}

            argumentCaptor<LoginSessionConfiguration> {
                verify(mockLoginSession).present(eq(mockLauncher), capture())
                val capturedConfiguration = firstValue
                assertEquals(testPersistentId, capturedConfiguration.persistentSessionId)
                Assertions.assertEquals(
                    LoginSessionConfiguration.Locale.EN,
                    capturedConfiguration.locale
                )
            }
        }

    @Test
    fun `login() - handles AttestationResult NotRequired without savedAttestation`() =
        runTest {
            whenever(mockGetPersistentId.invoke()).thenReturn(testPersistentId)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.NotRequired(null)
            )

            startRemoteLogin.login(mockLauncher) {}

            argumentCaptor<LoginSessionConfiguration> {
                verify(mockLoginSession).present(eq(mockLauncher), capture())
                val capturedConfiguration = firstValue
                assertEquals(testPersistentId, capturedConfiguration.persistentSessionId)
                Assertions.assertEquals(
                    LoginSessionConfiguration.Locale.EN,
                    capturedConfiguration.locale
                )
            }
        }

    @Test
    fun `login() - loginSession present() throws error`(): Unit =
        runTest {
            val error = Error("Error")
            val expectedWrappedException = LoginException(error)
            whenever(mockGetPersistentId.invoke()).thenReturn(testPersistentId)
            whenever(mockAppIntegrity.getClientAttestation()).thenReturn(
                AttestationResult.NotRequired(null)
            )
            whenever(mockLoginSession.present(any(), any())).thenThrow(error)

            startRemoteLogin.login(mockLauncher) {}

            verify(mockLogger).error(
                expectedWrappedException.javaClass.simpleName,
                error.message ?: "No message",
                expectedWrappedException
            )
        }
}
