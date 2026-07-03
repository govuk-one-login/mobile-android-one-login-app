package uk.gov.onelogin.core.tokens.domain.save

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(uk.gov.onelogin.core.extensions.CoroutinesTestExtension::class)
class SaveToTokenSecureStoreTest {
    private lateinit var useCase: SaveToTokenSecureStore
    private val mockSecureStore: SecureStoreAsyncV2 = mock()
    private val logger = MemorisedLogger()

    private val expectedStoreKey: String = "key"
    private val expectedStoreValue: String = "value"

    @BeforeEach
    fun setUp() {
        useCase = SaveToTokenSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun `does not throw - when SecureStorageErrorV2 thrown`() =
        runTest {
            whenever(mockSecureStore.upsert(any(), any())).thenThrow(
                SecureStorageErrorV2(Exception("Some error")),
            )

            assertDoesNotThrow {
                useCase.invoke(expectedStoreKey, expectedStoreValue)
            }

            assertThat(logger, hasItem(hasMessage("java.lang.Exception: Some error")))
        }

    @Test
    fun `saves value successfully`() =
        runTest {
            useCase.invoke(expectedStoreKey, expectedStoreValue)

            verify(mockSecureStore).upsert(
                expectedStoreKey,
                expectedStoreValue,
            )

            assertThat(logger, hasSize(0))
        }
}
