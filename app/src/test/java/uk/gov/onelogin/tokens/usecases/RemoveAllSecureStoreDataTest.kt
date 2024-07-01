package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.tokens.Keys

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class RemoveAllSecureStoreDataTest {
    private lateinit var useCase: RemoveAllSecureStoreData
    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockSecureStore: SecureStore = mock()

    @BeforeEach
    fun setUp() {
        useCase = RemoveAllSecureStoreDataImpl(mockSecureStore)
    }

    @Test
    fun `removes access token and id token`() = runTest {
        useCase.invoke(mockFragmentActivity)

        verify(mockSecureStore).delete(
            Keys.ACCESS_TOKEN_KEY,
            mockFragmentActivity
        )
        verify(mockSecureStore).delete(
            Keys.ID_TOKEN_KEY,
            mockFragmentActivity
        )
    }

    @Test
    @Suppress("SwallowedException")
    fun `removes access token and id token - no exception`() = runTest {
        whenever(
            mockSecureStore.delete(
                Keys.ACCESS_TOKEN_KEY,
                mockFragmentActivity
            )
        ).thenThrow(SecureStorageError(Exception("something went wrong")))

        try {
            useCase.invoke(mockFragmentActivity)
        } catch (e: SecureStorageError) {
            Assertions.fail("No exception should be thrown")
        }
    }
}
