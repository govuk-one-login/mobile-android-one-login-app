package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
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
import uk.gov.onelogin.extensions.CoroutinesTestExtension

@ExtendWith(CoroutinesTestExtension::class)
class SaveToSecureStoreTest {
    private lateinit var useCase: SaveToSecureStore
    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockSecureStore: SecureStore = mock()

    private val expectedStoreKey: String = "key"
    private val expectedStoreValue: String = "value"

    @BeforeEach
    fun setUp() {
        useCase = SaveToSecureStoreImpl(mockSecureStore)
    }

    @Test
    fun `does not throw - when SecureStorageError thrown`() = runTest {
        whenever(mockSecureStore.upsert(any(), any(), any())).thenThrow(
            SecureStorageError(Exception("Some error"))
        )

        assertDoesNotThrow {
            useCase.invoke(mockFragmentActivity, expectedStoreKey, expectedStoreValue)
        }
    }

    @Test
    fun `saves value successfully`() = runTest {
        useCase.invoke(mockFragmentActivity, expectedStoreKey, expectedStoreValue)

        verify(mockSecureStore).upsert(
            expectedStoreKey,
            expectedStoreValue,
            mockFragmentActivity
        )
    }
}
