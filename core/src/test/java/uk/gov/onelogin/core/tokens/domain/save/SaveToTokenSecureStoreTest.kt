package uk.gov.onelogin.core.tokens.domain.save

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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(uk.gov.onelogin.core.extensions.CoroutinesTestExtension::class)
class SaveToTokenSecureStoreTest {
    private lateinit var useCase: SaveToTokenSecureStore
    private val mockSecureStore: SecureStore = mock()
    private val logger = SystemLogger()

    private val expectedStoreKey: String = "key"
    private val expectedStoreValue: String = "value"

    @BeforeEach
    fun setUp() {
        useCase = SaveToTokenSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun `does not throw - when SecureStorageError thrown`() =
        runTest {
            whenever(mockSecureStore.upsert(any(), any())).thenThrow(
                SecureStorageError(Exception("Some error")),
            )

            assertDoesNotThrow {
                useCase.invoke(expectedStoreKey, expectedStoreValue)
            }

            assertTrue(logger.contains("java.lang.Exception: Some error"))
        }

    @Test
    fun `saves value successfully`() =
        runTest {
            useCase.invoke(expectedStoreKey, expectedStoreValue)

            verify(mockSecureStore).upsert(
                expectedStoreKey,
                expectedStoreValue,
            )

            assertEquals(0, logger.size)
        }
}
