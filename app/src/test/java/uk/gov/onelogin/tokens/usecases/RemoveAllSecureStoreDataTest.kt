package uk.gov.onelogin.tokens.usecases

import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.tokens.Keys

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class RemoveAllSecureStoreDataTest {
    private lateinit var useCase: RemoveAllSecureStoreData
    private val mockSecureStore: SecureStore = mock()

    @BeforeEach
    fun setUp() {
        useCase = RemoveAllSecureStoreDataImpl(mockSecureStore)
    }

    @Test
    fun `removes access token and id token`() = runTest {
        useCase.invoke()

        verify(mockSecureStore).delete(
            Keys.ACCESS_TOKEN_KEY
        )
        verify(mockSecureStore).delete(
            Keys.ID_TOKEN_KEY
        )
    }

    @Test
    fun `secure store exception is propagated up`() = runTest {
        whenever(
            mockSecureStore.delete(
                Keys.ACCESS_TOKEN_KEY
            )
        ).thenThrow(SecureStorageError(Exception("something went wrong")))

        val exception: SecureStorageError = assertThrows(SecureStorageError::class.java) {
            useCase.invoke()
        }
        assertEquals(SecureStoreErrorType.GENERAL, exception.type)
        assertTrue(exception.message!!.contains("something went wrong"))
    }
}
