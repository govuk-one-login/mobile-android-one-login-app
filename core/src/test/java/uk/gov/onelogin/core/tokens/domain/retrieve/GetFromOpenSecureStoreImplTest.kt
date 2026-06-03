package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.android.securestore.error.SecureStoreErrorTypeV2
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.utils.MockitoHelper
import java.lang.Exception
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetFromOpenSecureStoreImplTest {
    private val secureStore: SecureStoreAsyncV2 = mock()
    private val logger = MemorisedLogger()
    private val useCase = GetFromOpenSecureStoreImpl(secureStore, logger)

    @Test
    fun verifySuccess() =
        runTest {
            val expected = mapOf("Key" to "Success")
            whenever(secureStore.retrieve(MockitoHelper.anyObject()))
                .thenReturn(expected)

            val result = useCase.invoke("Key")
            assertEquals(expected, result)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun verifyFailure() =
        runTest {
            val error =
                SecureStorageErrorV2(
                    Exception("Exception!"),
                    SecureStoreErrorTypeV2.RECOVERABLE,
                )
            whenever(secureStore.retrieve(MockitoHelper.anyObject()))
                .thenThrow(error)

            assertThat(logger, hasSize(0))

            val result = useCase.invoke("Key")

            assertNull(result)
            assertThat(logger, hasSize(greaterThan(0)))
        }
}
