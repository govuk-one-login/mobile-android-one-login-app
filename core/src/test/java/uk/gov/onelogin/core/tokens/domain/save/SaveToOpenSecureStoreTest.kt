package uk.gov.onelogin.core.tokens.domain.save

import io.ktor.util.date.getTimeMillis
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
import uk.gov.onelogin.core.extensions.CoroutinesTestExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class SaveToOpenSecureStoreTest {
    private lateinit var useCase: SaveToOpenSecureStore
    private val mockSecureStore: SecureStoreAsyncV2 = mock()
    private val logger = MemorisedLogger()

    private val expectedStoreStringKey: String = "key"
    private val expectedStoreValueString: String = "value"
    private val expectedStoreIntKey: String = "keyInt"
    private val expectedStoreValueInt: Int = 1
    private val expectedStoreValueNumber: Number = getTimeMillis()

    @BeforeEach
    fun setUp() {
        useCase = SaveToOpenSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun `does not throw - when SecureStorageErrorV2 thrown when saving strings`() =
        runTest {
            whenever(mockSecureStore.upsert(any(), any())).thenThrow(
                SecureStorageErrorV2(Exception("Some error")),
            )

            assertDoesNotThrow {
                useCase.save(expectedStoreStringKey, expectedStoreValueString)
            }

            assertThat(logger, hasItem(hasMessage("java.lang.Exception: Some error")))
        }

    @Test
    fun `does not throw - when SecureStorageErrorV2 thrown when saving integers`() =
        runTest {
            whenever(mockSecureStore.upsert(any(), any())).thenThrow(
                SecureStorageErrorV2(Exception("Some error")),
            )

            assertDoesNotThrow {
                useCase.save(expectedStoreIntKey, expectedStoreValueInt)
            }

            assertThat(logger, hasItem(hasMessage("java.lang.Exception: Some error")))
        }

    @Test
    fun `saves value successfully (String, String)`() =
        runTest {
            useCase.save(expectedStoreStringKey, expectedStoreValueString)

            verify(mockSecureStore).upsert(
                expectedStoreStringKey,
                expectedStoreValueString,
            )

            assertThat(logger, hasSize(0))
        }

    @Test
    fun `saves value successfully (String, Number)`() =
        runTest {
            useCase.save(expectedStoreStringKey, expectedStoreValueNumber)

            verify(mockSecureStore).upsert(
                expectedStoreStringKey,
                expectedStoreValueNumber.toString(),
            )

            assertThat(logger, hasSize(0))
        }
}
