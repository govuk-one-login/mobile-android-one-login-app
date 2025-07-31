package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.utils.MockitoHelper

class GetFromOpenSecureStoreImplTest {
    private val secureStore: SecureStore = mock()
    private val logger = SystemLogger()
    private val useCase = GetFromOpenSecureStoreImpl(secureStore, logger)

    @Test
    fun verifySuccess() = runTest {
        val expected = mapOf("Key" to "Success")
        whenever(secureStore.retrieve(MockitoHelper.anyObject()))
            .thenReturn(RetrievalEvent.Success(expected))

        val result = useCase.invoke("Key")
        assertEquals(expected, result)
        assertEquals(0, logger.size)
    }

    @Test
    fun verifyFailure() = runTest {
        whenever(secureStore.retrieve(MockitoHelper.anyObject()))
            .thenReturn(
                RetrievalEvent.Failed(
                    SecureStoreErrorType.NOT_FOUND,
                    "Not found"
                )
            )

        val result = useCase.invoke("Key")
        assertNull(result)
        assertTrue(logger.contains("Reason: Not found"))
    }
}
