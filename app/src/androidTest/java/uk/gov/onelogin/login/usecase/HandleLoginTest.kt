package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry

@HiltAndroidTest
class HandleLoginTest : TestCase() {
    private val mockGetTokenExpiry: GetTokenExpiry = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockGetFromSecureStore: GetFromSecureStore = mock()

    private val useCase = HandleLoginImpl(
        mockGetTokenExpiry,
        mockTokenRepository,
        mockGetFromSecureStore
    )

    @Test
    fun tokenExpired() {
        val expiredTime = System.currentTimeMillis() - 1L
        whenever(mockGetTokenExpiry()).thenReturn(expiredTime)

        runBlocking {
            val loginSuccess = useCase(composeTestRule.activity as FragmentActivity)

            assertFalse(loginSuccess)
        }
    }

    @Test
    fun tokenNull() {
        whenever(mockGetTokenExpiry()).thenReturn(null)

        runBlocking {
            val loginSuccess = useCase(composeTestRule.activity as FragmentActivity)

            assertFalse(loginSuccess)
        }
    }

    @Test
    fun accessTokenNull() {
        val unexpiredTime = System.currentTimeMillis() + 100000L
        whenever(mockGetTokenExpiry()).thenReturn(unexpiredTime)
        runBlocking {
            whenever(mockGetFromSecureStore(any(), any())).thenReturn(null)

            val loginSuccess = useCase(composeTestRule.activity as FragmentActivity)

            assertFalse(loginSuccess)
        }
    }

    @Test
    fun goodLogin() {
        val token = "Token"
        val unexpiredTime = System.currentTimeMillis() + 100000L
        whenever(mockGetTokenExpiry()).thenReturn(unexpiredTime)
        runBlocking {
            whenever(mockGetFromSecureStore(any(), any())).thenReturn(token)

            val loginSuccess = useCase(composeTestRule.activity as FragmentActivity)

            verify(mockTokenRepository).setTokenResponse(
                TokenResponse(
                    accessToken = token,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
            assertTrue(loginSuccess)
        }
    }
}
