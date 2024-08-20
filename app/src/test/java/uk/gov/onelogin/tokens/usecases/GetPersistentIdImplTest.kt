package uk.gov.onelogin.tokens.usecases

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys

class GetPersistentIdImplTest {

    private val expectedPersitentId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
    private val idTokenWithId =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN2Q5LTk4NzgxZ" +
            "WJkZjkzZCJ9.eyJhdWQiOiJHRVV6a0V6SVFVOXJmYmdBWmJzal9fMUVOUU0iLCJpc3MiOiJodHRwcz" +
            "ovL3Rva2VuLmJ1aWxkLmFjY291bnQuZ292LnVrIiwic3ViIjoiOWQwZjIxZGUtMmZkNy00MjdiLWE2" +
            "ZGYtMDdjZDBkOTVlM2I2IiwicGVyc2lzdGVudF9pZCI6ImNjODkzZWNlLWI2YmQtNDQ0ZC05YmI0LW" +
            "RlYzZmNTc3OGU1MCIsImlhdCI6MTcyMTk5ODE3OCwiZXhwIjoxNzIxOTk4MzU4LCJub25jZSI6InRl" +
            "c3Rfbm9uY2UiLCJlbWFpbCI6Im1vY2tAZW1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.G" +
            "1uQ9z2i-214kEmmtK7hEHRsgqJdk7AXjz_CaJDiuuqSyHZ4W48oE1karDBA-pKWpADdBpHeUC-eCj" +
            "jfBObjOg"
    private val idTokenWithoutId = "eyJhbGciOiJIUzI1NiJ9" +
        ".e30." + // no id in the payload
        "ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo"
    private val tokenResponse: TokenResponse = mock()
    private val tokenRepository: TokenRepository = mock()
    private val mockGetFromOpenSecureStore: GetFromOpenSecureStore = mock()

    private val sut = GetPersistentIdImpl(tokenRepository, mockGetFromOpenSecureStore)

    @BeforeEach
    fun setUp() {
        whenever(tokenResponse.idToken).thenReturn(idTokenWithId)
        whenever(tokenRepository.getTokenResponse()).thenReturn(tokenResponse)
    }

    @Test
    fun successScenario() = runTest {
        // Given id token contains id
        whenever(tokenResponse.idToken).thenReturn(idTokenWithId)
        whenever(tokenRepository.getTokenResponse()).thenReturn(tokenResponse)

        val idResponse = sut.invoke()
        assertEquals(expectedPersitentId, idResponse)
        verify(mockGetFromOpenSecureStore, times(0)).invoke(any())
    }

    @Test
    fun missingIdScenarioAndNoSS() = runTest {
        // Given id token is missing the id
        whenever(tokenResponse.idToken).thenReturn(idTokenWithoutId)
        whenever(tokenRepository.getTokenResponse()).thenReturn(tokenResponse)

        val idResponse = sut.invoke()
        assertEquals(null, idResponse)
    }

    @Test
    fun missingTokenScenarioAndNoSS() = runTest {
        // Given token is null
        whenever(tokenRepository.getTokenResponse()).thenReturn(null)

        val idResponse = sut.invoke()
        assertEquals(null, idResponse)
    }

    @Test
    fun missingIdTokenScenarioAndNoSS() = runTest {
        // Given id token is null
        whenever(tokenResponse.idToken).thenReturn(null)

        val idResponse = sut.invoke()
        assertEquals(null, idResponse)
    }

    @Test
    fun missingTokenScenarioAndPresentSS() = runTest {
        // Given token is null
        whenever(tokenRepository.getTokenResponse()).thenReturn(null)
        whenever(mockGetFromOpenSecureStore(Keys.PERSISTENT_ID_KEY)).thenReturn(
            expectedPersitentId
        )

        val idResponse = sut.invoke()
        assertEquals(expectedPersitentId, idResponse)
    }
}
