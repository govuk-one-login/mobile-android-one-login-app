package uk.gov.onelogin.tokens.usecases

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.repositiories.TokenRepository

class GetEmailImplTest {

    private val expectedEmail = "email@mail.com"
    private val idToken = "eyJhbGciOiJIUzI1NiJ9" +
        ".eyJlbWFpbCI6ImVtYWlsQG1haWwuY29tIn0" + // payload contains "email": "email@mail.com"
        ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
    private val tokenResponse: TokenResponse = mock()
    private val tokenRepository: TokenRepository = mock()

    val sut = GetEmailImpl(tokenRepository)

    @BeforeEach
    fun setUp() {
        whenever(tokenResponse.idToken).thenReturn(idToken)
        whenever(tokenRepository.getTokenResponse()).thenReturn(tokenResponse)
    }

    @Test
    operator fun invoke() {
        val emailResponse = sut.invoke()
        assertEquals(expectedEmail, emailResponse)
    }
}
