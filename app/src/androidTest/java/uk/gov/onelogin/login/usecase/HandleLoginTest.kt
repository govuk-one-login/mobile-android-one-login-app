package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.tokens.usecases.IsAccessTokenExpired

@HiltAndroidTest
class HandleLoginTest : TestCase() {
    private val mockGetTokenExpiry: GetTokenExpiry = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockGetFromTokenSecureStore: GetFromTokenSecureStore = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockIsAccessTokenExpired: IsAccessTokenExpired = mock()

    private val useCase = HandleLoginImpl(
        mockGetTokenExpiry,
        mockTokenRepository,
        mockIsAccessTokenExpired,
        mockGetFromTokenSecureStore,
        mockBioPrefHandler
    )

    @Test
    fun tokenExpired_refreshLogin() {
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.PASSCODE)

        runBlocking {
            useCase(
                composeTestRule.activity as FragmentActivity
            ) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun tokenNull_refreshLogin() {
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetTokenExpiry()).thenReturn(null)
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.BIOMETRICS)

        runBlocking {
            useCase(composeTestRule.activity as FragmentActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun bioPrefNone_refreshLogin() {
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.NONE)

        runBlocking {
            useCase(composeTestRule.activity as FragmentActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun nonSuccessResponseFromGetFromSecureStore() {
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.PASSCODE)

        runBlocking {
            whenever(mockGetFromTokenSecureStore(any(), any(), any())).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
            }

            useCase(composeTestRule.activity as FragmentActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }

            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }
    }

    @Test
    fun goodLogin() {
        val token = "Token"
        val unexpiredTime = System.currentTimeMillis() + 100000L
        whenever(mockGetTokenExpiry()).thenReturn(unexpiredTime)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.PASSCODE)

        runBlocking {
            whenever(mockGetFromTokenSecureStore(any(), any(), any())).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(token)
                )
            }

            useCase(composeTestRule.activity as FragmentActivity) {
                assertEquals(LocalAuthStatus.Success(token), it)
            }

            verify(mockTokenRepository).setTokenResponse(
                TokenResponse(
                    accessToken = token,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
        }
    }
}
