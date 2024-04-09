package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStorageError
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.onelogin.R
import uk.gov.onelogin.extensions.CoroutinesTestExtension

@ExtendWith(CoroutinesTestExtension::class)
class GetFromSecureStoreTest {
    private lateinit var useCase: GetFromSecureStore
    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockSecureStore: SecureStore = mock()

    private val expectedPromptTitle = "Some Title"
    private val expectedAuthPromptConfig = AuthenticatorPromptConfiguration(expectedPromptTitle)
    private val expectedStoreKey: String = "key"

    @BeforeEach
    fun setUp() {
        whenever(mockFragmentActivity.getString(R.string.app_authenticationDialogueTitle))
            .thenReturn(expectedPromptTitle)
        useCase = GetFromSecureStoreImpl(mockSecureStore)
    }

    @Test
    fun `returns null - when exception thrown`() = runTest {
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenThrow(
            SecureStorageError(Exception("Some error"))
        )

        val result = useCase.invoke(mockFragmentActivity, expectedStoreKey)
        assertNull(result)
    }

    @Test
    fun `returns value successfully`() = runTest {
        val expectedValue = "expectedValue"
        whenever(
            mockSecureStore.retrieveWithAuthentication(
                expectedStoreKey,
                expectedAuthPromptConfig,
                mockFragmentActivity
            )
        ).thenReturn(expectedValue)

        val resultValue = useCase.invoke(mockFragmentActivity, expectedStoreKey)
        assertEquals(expectedValue, resultValue)
    }
}
