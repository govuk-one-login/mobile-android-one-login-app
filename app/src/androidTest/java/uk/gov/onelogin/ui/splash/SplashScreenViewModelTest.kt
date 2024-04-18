package uk.gov.onelogin.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.ui.home.HomeRoutes

@HiltAndroidTest
class SplashScreenViewModelTest : TestCase() {
    private val mockHandleLogin: HandleLogin = mock()

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

    @Test
    fun loginFails() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.SecureStoreError
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)
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
            assertEquals(HomeRoutes.START, viewModel.next.value)
        }
    }

    @Test
    fun loginRequiresRefresh() = runTest {
        whenever(mockHandleLogin.invoke(eq(composeTestRule.activity as FragmentActivity), any()))
            .thenAnswer {
                (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.RefreshToken
                )
            }
        viewModel.login(composeTestRule.activity as FragmentActivity)

        Handler(Looper.getMainLooper()).post {
            assertEquals(LoginRoutes.WELCOME, viewModel.next.value)
        }
    }

    @Test
    fun loginThrowsUserCancelled() = runTest {
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
}
