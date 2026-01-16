package uk.gov.onelogin.core.tokens.domain.remove

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.extensions.CoroutinesTestExtension
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class RemoveAllSecureStoreDataTest {
    private lateinit var useCase: RemoveAllSecureStoreData
    private val tokenSecureStore: SecureStore = mock()
    private val openSecureStore: SecureStore = mock()
    private val logger = SystemLogger()

    @BeforeEach
    fun setUp() {
        useCase = RemoveAllSecureStoreDataImpl(tokenSecureStore, openSecureStore, logger)
    }

    @Test
    fun `removes access token and id token`() =
        runTest {
            val result = useCase.clean()

            verify(tokenSecureStore).deleteAll()
            verify(openSecureStore).deleteAll()
            assertEquals(Result.success(Unit), result)
            assertEquals(0, logger.size)
        }

    @Test
    fun `token secure store exception is propagated up`() =
        runTest {
            whenever(
                tokenSecureStore.deleteAll(),
            ).thenThrow(SecureStorageError(Exception("something went wrong")))

            val result = useCase.clean()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is SecureStorageError)
            assertTrue(result.exceptionOrNull()?.message!!.contains("something went wrong"))
            assertTrue(logger.contains("java.lang.Exception: something went wrong"))
        }

    @Test
    fun `open secure store exception is propagated up`() =
        runTest {
            whenever(
                openSecureStore.deleteAll(),
            ).thenThrow(SecureStorageError(Exception("something went wrong")))

            val result = useCase.clean()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is SecureStorageError)
            assertTrue(result.exceptionOrNull()?.message!!.contains("something went wrong"))
            assertTrue(logger.contains("java.lang.Exception: something went wrong"))
        }
}
