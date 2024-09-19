package uk.gov.onelogin.login.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.mainnav.nav.MainNavRoutes

@HiltAndroidTest
class SplashScreenViewModelTest : TestCase() {
    private val mockHandleLogin: HandleLogin = mock()
    private val mockLifeCycleOwner: LifecycleOwner = mock()

    private val stringObserver: Observer<String> = mock()

    private val viewModel = SplashScreenViewModel(
        mockHandleLogin
    )

    @Before
    fun setup() {
        Handler(Looper.getMainLooper()).post {
            viewModel.next.observeForever(stringObserver)
        }
    }

    @After
    fun tearDown() {
        Handler(Looper.getMainLooper()).post {
            viewModel.next.removeObserver(stringObserver)
        }
    }

    @Test
    fun loginFailsWithSecureStoreError() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.SecureStoreError
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertEquals(LoginRoutes.SIGN_IN_ERROR, viewModel.next.value)
        }
    }

    @Test
    fun loginFailsWithBioCheckFailed() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.BioCheckFailed
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertEquals(null, viewModel.next.value)
        }
    }

    @Test
    fun loginSuccess() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success("token")
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertEquals(MainNavRoutes.START, viewModel.next.value)
        }
    }

    @Test
    fun loginRequiresRefresh() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.ManualSignIn
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertEquals(LoginRoutes.WELCOME, viewModel.next.value)
        }
    }

    @Test
    fun loginReturnsUserCancelled() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.UserCancelled
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertNull(viewModel.next.value)
            assertTrue(viewModel.showUnlock.value)
        }
    }

    @Test
    fun ignoreFirstLoginCallFromLockScreen() = runTest {
        // GIVEN the call was made from the lock screen
        val fromLockScreen = true

        // AND on resume called only once
        viewModel.onResume(mockLifeCycleOwner)

        // WHEN we call login
        viewModel.login(composeTestRule.activity as FragmentActivity, fromLockScreen)

        // THEN do NOT login (as the app will be going to background shortly)
        verify(mockHandleLogin, never()).invoke(any(), any())
    }

    @Test
    fun allowsSubsequentLoginCallsFromLockScreen() = runTest {
        // GIVEN the call was made from the lock screen
        val fromLockScreen = true

        // AND on resume called more than once
        viewModel.onResume(mockLifeCycleOwner)
        viewModel.onResume(mockLifeCycleOwner)

        // WHEN we call login
        viewModel.login(composeTestRule.activity, fromLockScreen)

        // THEN do NOT login (as the app will be going to background)
        verify(mockHandleLogin, times(1)).invoke(any(), any())
    }
}
