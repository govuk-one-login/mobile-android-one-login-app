package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.state.LocalAuthStatus

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
    fun secureStoreFailsWithGeneral() = runTest {
        val flow = flow {
            emit(RetrievalEvent.Failed(SecureStoreErrorType.GENERAL))
        }
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenReturn(flow)
        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.SecureStoreError, it)
        }
    }

    @Test
    fun secureStoreFailsWithUserCancelled() = runTest {
        val flow = flow {
            emit(RetrievalEvent.Failed(SecureStoreErrorType.USER_CANCELED_BIO_PROMPT))
        }
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenReturn(flow)
        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.UserCancelled, it)
        }
    }

    @Test
    fun secureStoreFailsWithBioFailed() = runTest {
        val flow = flow {
            emit(RetrievalEvent.Failed(SecureStoreErrorType.FAILED_BIO_PROMPT))
        }
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenReturn(flow)
        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.BioCheckFailed, it)
        }
    }

    @Test
    fun success() = runTest {
        val expectedValue = "expectedValue"
        val flow = flow {
            emit(RetrievalEvent.Success(expectedValue))
        }
        whenever(mockSecureStore.retrieveWithAuthentication(any(), any(), any())).thenReturn(flow)
        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.Success(expectedValue), it)
        }
    }
}
