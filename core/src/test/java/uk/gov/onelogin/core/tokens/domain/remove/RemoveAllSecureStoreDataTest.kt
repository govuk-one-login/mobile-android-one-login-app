package uk.gov.onelogin.core.tokens.domain.remove

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.extensions.CoroutinesTestExtension
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class RemoveAllSecureStoreDataTest {
    private lateinit var useCase: RemoveAllSecureStoreData
    private val tokenSecureStore: SecureStoreAsyncV2 = mock()
    private val openSecureStore: SecureStoreAsyncV2 = mock()
    private val logger = MemorisedLogger()

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
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `token secure store exception is propagated up`() =
        runTest {
            whenever(
                tokenSecureStore.deleteAll(),
            ).thenThrow(SecureStorageErrorV2(Exception("something went wrong")))

            val result = useCase.clean()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is SecureStorageErrorV2)
            assertTrue(result.exceptionOrNull()?.message!!.contains("something went wrong"))
            assertThat(logger, hasItem(hasMessage("java.lang.Exception: something went wrong")))
        }

    @Test
    fun `open secure store exception is propagated up`() =
        runTest {
            whenever(
                openSecureStore.deleteAll(),
            ).thenThrow(SecureStorageErrorV2(Exception("something went wrong")))

            val result = useCase.clean()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is SecureStorageErrorV2)
            assertTrue(result.exceptionOrNull()?.message!!.contains("something went wrong"))
            assertThat(logger, hasItem(hasMessage("java.lang.Exception: something went wrong")))
        }
}
