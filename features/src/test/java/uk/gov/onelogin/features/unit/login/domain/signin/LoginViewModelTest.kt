package uk.gov.onelogin.features.unit.login.domain.signin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasCustomKeys
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasLogEntry
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.utils.TestActivityResultLauncher
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.login.LoginViewModel
import uk.gov.onelogin.features.login.domain.signin.remotelogin.TestRemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class LoginViewModelTest {
    private val fragmentActivity: FragmentActivity = mock()
    private val activityResultLauncher = TestActivityResultLauncher<Intent>()
    private val navigator: Navigator = mock()
    private val onlineChecker: OnlineChecker = mock()
    private val remoteLogin = TestRemoteLogin()
    private val getPersistentId: GetPersistentId = mock()
    private val logger = MemorisedLogger()
    private val signOutUseCase: SignOutUseCase = mock()
    private val localAuthPrefResetUseCase: LocalAuthPrefResetUseCase = mock()

    private val viewModel =
        LoginViewModel(
            navigator,
            onlineChecker,
            remoteLogin,
            getPersistentId,
            signOutUseCase,
            localAuthPrefResetUseCase,
            logger
        )

    @BeforeEach
    fun setUp() = runTest {
        givenOnline()
        givenPersistentId("test")
    }

    @Test
    fun `startLoginActivity starts remote login`() =
        runTest {
            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            assertTrue(remoteLogin.started)
        }

    @Test
    fun `startLoginActivity resets local auth preference`() =
        runTest {
            viewModel.startLoginActivity(activityResultLauncher, true)

            verify(localAuthPrefResetUseCase).reset()
        }

    @Test
    fun `given first time user & persistent id is null, startLoginActivity starts remote login`() =
        runTest {
            givenPersistentId(null)

            viewModel.startLoginActivity(activityResultLauncher, false)

            assertTrue(viewModel.loading.value)
            assertTrue(remoteLogin.started)
        }

    @Test
    fun `given offline, startLoginActivity navigates to offline error`() {
        givenOnline(false)

        viewModel.startLoginActivity(activityResultLauncher, false)

        verify(navigator).navigate(ErrorRoutes.Offline, false)
        assertFalse(remoteLogin.started)
    }

    @Test
    fun `given re-auth & persistent id is null, startLoginActivity signs out & navigates to re-auth error`() =
        runTest {
            givenPersistentId(null)

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
            assertFalse(remoteLogin.started)
        }

    @Test
    fun `given re-auth & persistent id is empty, startLoginActivity signs out & navigates to re-auth error`() =
        runTest {
            givenPersistentId("")

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
            assertFalse(remoteLogin.started)
        }

    @Test
    fun `given re-auth & persistent id is null & sign out error, startLoginActivity goes to unrecoverable error`() =
        runTest {
            givenPersistentId(null)
            givenSignOutThrows()

            viewModel.startLoginActivity(activityResultLauncher, true)

            assertTrue(viewModel.loading.value)
            verify(navigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertFalse(remoteLogin.started)
        }

    @Test
    fun `given login started, abortLogin cancels start job and resets loading`() =
        runTest {
            remoteLogin.startWillComplete = false

            viewModel.startLoginActivity(activityResultLauncher, false)
            assertTrue(remoteLogin.started)
            assertTrue(viewModel.loading.value)

            viewModel.abortLogin()

            assertFalse(viewModel.loading.value)
            assertTrue(remoteLogin.startCancelled)
        }

    @Test
    fun `handleLoginActivityResult finalises remote login`() =
        runTest {
            val activityResult = givenActivityResult()

            viewModel.handleLoginActivityResult(
                activityResult,
                activity = fragmentActivity
            )

            assertTrue(viewModel.loading.value)
            assertNotNull(remoteLogin.finalisedWith)
            assertSame(activityResult.data!!, remoteLogin.finalisedWith?.intent)
        }

    @Test
    fun `given result code is not RESULT_OK, handleLoginActivityResult does not finalise login`() =
        runTest {
            val activityResult = givenActivityResult(resultCode = Activity.RESULT_CANCELED)

            viewModel.handleLoginActivityResult(activityResult, activity = fragmentActivity)

            assertNull(remoteLogin.finalisedWith)
        }

    @Test
    fun `given result code is not RESULT_OK, handleLoginActivityResult stops loading`() =
        runTest {
            val activityResult = givenActivityResult(resultCode = Activity.RESULT_CANCELED)

            viewModel.handleLoginActivityResult(activityResult, activity = fragmentActivity)

            assertFalse(viewModel.loading.value)
        }

    @Test
    fun `given intent data is null, handleLoginActivityResult does not finalise login`() =
        runTest {
            val activityResult = givenActivityResult(data = false)

            viewModel.handleLoginActivityResult(activityResult, activity = fragmentActivity)

            assertNull(remoteLogin.finalisedWith)
        }

    @Test
    fun `given intent data is null, handleLoginActivityResult stops loading`() =
        runTest {
            val activityResult = givenActivityResult(data = false)

            viewModel.handleLoginActivityResult(activityResult, activity = fragmentActivity)

            assertFalse(viewModel.loading.value)
        }

    @Test
    fun `given intent data is null, handleLoginActivityResult logs error`() =
        runTest {
            val activityResult = givenActivityResult(data = false)

            viewModel.handleLoginActivityResult(activityResult, activity = fragmentActivity)

            assertThat(
                logger,
                hasLogEntry(
                    hasItem(
                        allOf(
                            hasMessage(LoginViewModel.NULL_INTENT_MSG),
                            hasCustomKeys(
                                contains(
                                    equalTo(componentKey(LoginViewModel.COMPONENT_LOGIN)),
                                    equalTo(actionKey(LoginViewModel.ACTION_START_LOGIN_RESULT))
                                )
                            ),
                        )
                    )
                )
            )
        }

    @Test
    fun `given login started, stopLoading stops loading`() =
        runTest {
            viewModel.startLoginActivity(activityResultLauncher, true)
            assertTrue(viewModel.loading.value)

            viewModel.stopLoading()
            assertFalse(viewModel.loading.value)
        }

    private fun givenOnline(online: Boolean = true) {
        whenever(onlineChecker.isOnline()).thenReturn(online)
    }

    private suspend fun givenPersistentId(persistentId: String?) {
        whenever(getPersistentId.invoke()).thenReturn(persistentId)
    }

    private suspend fun givenSignOutThrows() {
        whenever(signOutUseCase.invoke()).thenThrow(SignOutError(Exception()))
    }

    private fun givenActivityResult(
        resultCode: Int = Activity.RESULT_OK,
        data: Boolean = true
    ): ActivityResult {
        val intent = if (data) mock<Intent>() else null

        return ActivityResult(resultCode, intent)
    }
}
