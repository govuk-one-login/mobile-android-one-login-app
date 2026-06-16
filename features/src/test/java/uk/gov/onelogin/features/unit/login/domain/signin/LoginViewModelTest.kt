package uk.gov.onelogin.features.unit.login.domain.signin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.login.LoginViewModel
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class LoginViewModelTest {
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var navigator: Navigator
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var remoteLogin: RemoteLogin
    private lateinit var getPersistentId: GetPersistentId
    private lateinit var logger: Logger
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var localAuthPrefResetUseCase: LocalAuthPrefResetUseCase

    private lateinit var mockIntent: Intent
    private lateinit var mockActivityResult: ActivityResult

    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setup() {
        activityResultLauncher = mock()
        fragmentActivity = mock()
        navigator = mock()
        remoteLogin = mock()
        onlineChecker = mock()
        signOutUseCase = mock()
        localAuthPrefResetUseCase = mock()
        getPersistentId = mock()
        mockIntent = mock()
        logger = mock()

        viewModel =
            LoginViewModel(
                navigator,
                onlineChecker,
                remoteLogin,
                getPersistentId,
                signOutUseCase,
                localAuthPrefResetUseCase,
                logger
            )
    }

    @Test
    fun `RE-AUTH - verify start login activity`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn("test")

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(localAuthPrefResetUseCase).reset()
            verify(remoteLogin).start(any())
        }

    @Test
    fun `RE-AUTH - check start login when persistent session id is null`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn(null)

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
            verify(remoteLogin, times(0)).start(any())
        }

    @Test
    fun `RE-AUTH - check start login when persistent session id is empty`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn("")

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
            verify(remoteLogin, times(0)).start(any())
        }

    @Test
    fun `RE-AUTH - check start login when persistent session id is null and sign out error`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn(null)
            whenever(signOutUseCase.invoke()).thenThrow(SignOutError(Exception()))

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(navigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            verify(remoteLogin, times(0)).start(any())
        }

    @Test
    fun `RE-AUTH - start login activity when offline`() {
        whenever(onlineChecker.isOnline()).thenReturn(false)

        viewModel.startLoginActivity(activityResultLauncher, true)

        verify(navigator).navigate(ErrorRoutes.Offline, false)
    }

    @Test
    fun `FIRST TIME USER - verify start login activity`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn("test")

            viewModel.startLoginActivity(activityResultLauncher, false)

            assertTrue(viewModel.loading.value)
            verify(localAuthPrefResetUseCase).reset()
            verify(remoteLogin).start(any())
        }

    @Test
    fun `FIRST TIME USER - check start login when persistent session id is null`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn(null)

            viewModel.startLoginActivity(activityResultLauncher, false)

            assertTrue(viewModel.loading.value)
            verify(localAuthPrefResetUseCase).reset()
            verify(remoteLogin).start(any())
        }

    @Test
    fun `FIRST TIME USER - check start login when persistent session id is empty`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn("")

            viewModel.startLoginActivity(activityResultLauncher, false)

            assertTrue(viewModel.loading.value)
            verify(localAuthPrefResetUseCase).reset()
            verify(remoteLogin).start(any())
        }

    @Test
    fun `FIRST TIME USER - start login activity when offline`() {
        whenever(onlineChecker.isOnline()).thenReturn(false)

        viewModel.startLoginActivity(activityResultLauncher, false)

        verify(navigator).navigate(ErrorRoutes.Offline, false)
    }

    @Test
    fun `check abort login works as expected`() =
        runTest {
            val mockIntent: Intent = mock()

            whenever(remoteLogin.finalise(eq(mockIntent), any(), any()))
                .thenAnswer {
                    runBlocking {
                        delay(10000)
                        assert(viewModel.loading.value)
                        viewModel.abortLogin(any(), any())
                    }
                }

            assertFalse(viewModel.loading.value)
        }

    @Test
    fun `handle result when intent data is null`() =
        runTest {
            mockActivityResult = ActivityResult(Activity.RESULT_OK, null)

            viewModel.handleLoginActivityResult(
                mockActivityResult,
                activity = fragmentActivity
            )

            assertFalse(viewModel.loading.value)
            verify(logger).error(
                eq(LoginViewModel.LOGIN_START_RESULT_TAG),
                eq(LoginViewModel.NULL_INTENT_MSG),
                any(),
                eq(
                    ErrorKeys.StringKey(
                        LoginViewModel.LOGIN_START_RESULT_TAG,
                        LoginViewModel.NULL_INTENT_MSG
                    )
                )
            )
            verify(remoteLogin, times(0))
                .finalise(eq(mockIntent), any(), any())
        }

    @Test
    fun `handle result when intent data is populated`() =
        runTest {
            val mockUri: Uri = mock()
            whenever(mockIntent.data).thenReturn(mockUri)
            mockActivityResult = ActivityResult(Activity.RESULT_OK, mockIntent)

            viewModel.handleLoginActivityResult(
                mockActivityResult,
                activity = fragmentActivity
            )

            assertTrue(viewModel.loading.value)
            verify(remoteLogin)
                .finalise(eq(mockIntent), any(), any())
        }

    @Test
    fun `handle result when result code is not RESULT_OK`() =
        runTest {
            val mockUri: Uri = mock()
            whenever(mockIntent.data).thenReturn(mockUri)
            mockActivityResult = ActivityResult(Activity.RESULT_CANCELED, mockIntent)

            viewModel.handleLoginActivityResult(
                mockActivityResult,
                activity = fragmentActivity
            )

            assertFalse(viewModel.loading.value)
            verify(remoteLogin, times(0))
                .finalise(eq(mockIntent), any(), any())
        }
}
