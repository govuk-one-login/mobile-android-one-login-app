package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class GetFromSecureStoreTest : TestCase() {
    private lateinit var useCase: GetFromSecureStore
    private val mockSecureStore: SecureStore = mock()

    private val expectedStoreKey: String = "key"

    @Before
    fun setUp() {
        useCase = GetFromSecureStoreImpl(mockSecureStore)
    }

    @Test
    fun throwsException() = runTest {
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenThrow(
            SecureStorageError(Exception("Some error"))
        )

        this.runCatching {
            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey
            )
        }.onFailure {
            assertEquals(SecureStorageError::class.java, it::class.java)
        }
    }

    @Test
    fun success() = runTest {
        val expectedValue = "expectedValue"
        whenever(
            mockSecureStore.retrieveWithAuthentication(
                eq(expectedStoreKey),
                any(),
                eq(composeTestRule.activity as FragmentActivity)
            )
        ).thenReturn(expectedValue)

        val resultValue = useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        )
        assertEquals(expectedValue, resultValue)
    }
}
