package uk.gov.onelogin.core.tokens.domain.save

import io.ktor.util.date.getTimeMillis
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.extensions.CoroutinesTestExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class SaveToOpenSecureStoreTest {
    private lateinit var useCase: SaveToOpenSecureStore
    private val mockSecureStore: SecureStore = mock()
    private val logger = SystemLogger()

    private val expectedStoreKey: String = "key"
    private val expectedStoreValueString: String = "value"
    private val expectedStoreValueNumber: Number = getTimeMillis()

    @BeforeEach
    fun setUp() {
        useCase = SaveToOpenSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun `does not throw - when SecureStorageError thrown`() =
        runTest {
            whenever(mockSecureStore.upsert(any(), any())).thenThrow(
                SecureStorageError(Exception("Some error"))
            )

            assertDoesNotThrow {
                useCase.save(expectedStoreKey, expectedStoreValueString)
            }

            assertTrue(logger.contains("java.lang.Exception: Some error"))
        }

    @Test
    fun `saves value successfully (String, String)`() =
        runTest {
            useCase.save(expectedStoreKey, expectedStoreValueString)

            verify(mockSecureStore).upsert(
                expectedStoreKey,
                expectedStoreValueString
            )

            assertEquals(0, logger.size)
        }

    @Test
    fun `saves value successfully (String, Number)`() =
        runTest {
            useCase.save(expectedStoreKey, expectedStoreValueNumber)

            verify(mockSecureStore).upsert(
                expectedStoreKey,
                expectedStoreValueNumber.toString()
            )

            assertEquals(0, logger.size)
        }
}
