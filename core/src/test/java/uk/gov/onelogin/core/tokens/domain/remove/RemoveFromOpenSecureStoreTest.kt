package uk.gov.onelogin.core.tokens.domain.remove

import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage

class RemoveFromOpenSecureStoreTest {
    private lateinit var useCase: RemoveFromOpenSecureStore

    private val openSecureStore: SecureStoreAsyncV2 = mock()

    private val logger = MemorisedLogger()

    @BeforeEach
    fun setUp() {
        useCase = RemoveFromOpenSecureStoreImpl(openSecureStore, logger)
    }

    @Test
    fun `remove from open secure store`() =
        runTest {
            useCase.remove("key")

            verify(openSecureStore).delete("key")
        }

    @Test
    fun `logs error when secure store throws SecureStorageErrorV2`() =
        runTest {
            val exception = SecureStorageErrorV2(Exception("something went wrong"))
            whenever(openSecureStore.delete("key")).thenThrow(exception)

            useCase.remove("key")

            assertThat(logger, hasItem(hasMessage("java.lang.Exception: something went wrong")))
        }
}
