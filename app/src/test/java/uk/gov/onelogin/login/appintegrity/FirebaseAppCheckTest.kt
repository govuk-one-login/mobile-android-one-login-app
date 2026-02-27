package uk.gov.onelogin.login.appintegrity

import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.appcheck.model.AppCheckToken
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FirebaseAppCheckTest {
    private val provider: FirebaseAppCheckProvider = mock()
    private val logger: Logger = mock()
    private lateinit var sut: AppChecker

    @Before
    fun setup() {
        sut = FirebaseAppCheck(provider, logger)
    }

    @Test
    fun `test initialise and get token success`() =
        runTest {
            whenever(provider.getToken()).thenReturn("token")

            val actual = sut.getAppCheckToken()

            assertEquals(Result.success(AppCheckToken("token")), actual)
            verify(provider).init()
        }

    @Test
    fun `test firebase provider get limited token returns Exception`() =
        runTest {
            val expected = Exception()
            whenever(provider.getToken()).thenThrow(expected)
            val actual = sut.getAppCheckToken()

            verify(provider).init()
            verify(logger).error(any(), any(), any())
            assertTrue(actual.isFailure)
            actual.onFailure {
                assertTrue(it is AppIntegrityException.Other)
                assertEquals(expected, it.cause)
            }
        }

    @Test
    fun `test firebase provider get limited token returns Firebase Exception`() =
        runTest {
            val expected = FirebaseNetworkException("network exception")
            whenever(provider.getToken()).thenThrow(expected)
            val actual = sut.getAppCheckToken()

            verify(provider).init()
            verify(logger).error(any(), any(), any())
            assertTrue(actual.isFailure)
            actual.onFailure {
                assertTrue(it is AppIntegrityException.FirebaseException)
                assertEquals(expected, it.cause)
            }
        }
}
