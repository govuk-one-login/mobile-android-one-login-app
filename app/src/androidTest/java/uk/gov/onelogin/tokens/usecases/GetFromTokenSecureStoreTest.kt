package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.state.LocalAuthStatus

@HiltAndroidTest
class GetFromTokenSecureStoreTest : TestCase() {
    private lateinit var useCase: GetFromTokenSecureStore
    private val mockSecureStore: SecureStore = mock()

    private val expectedStoreKey: String = "key"

    @Before
    fun setUp() {
        useCase = GetFromTokenSecureStoreImpl(mockSecureStore)
    }

    @Test
    fun secureStoreFailsWithGeneral() = runTest {
        mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.GENERAL))

        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.SecureStoreError, it)
        }
    }

    @Test
    fun secureStoreFailsWithUserCancelled() = runTest {
        mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.USER_CANCELED_BIO_PROMPT))

        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.UserCancelled, it)
        }
    }

    @Test
    fun secureStoreFailsWithBioFailed() = runTest {
        mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.FAILED_BIO_PROMPT))
        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey
        ) {
            assertEquals(LocalAuthStatus.BioCheckFailed, it)
        }
    }

    @Test
    fun success() = runTest {
        val expectedKey = "expectedKey"
        val expectedValue = "expectedValue"
        val expectedResult = mapOf(expectedKey to expectedValue)
        mockSecureStore(RetrievalEvent.Success(expectedResult))

        useCase.invoke(
            composeTestRule.activity as FragmentActivity,
            expectedStoreKey,
            "test"
        ) {
            assertEquals(LocalAuthStatus.Success(expectedResult), it)
        }
    }

    private suspend fun mockSecureStore(returnResult: RetrievalEvent) {
        whenever(
            mockSecureStore.retrieveWithAuthentication(
                anyVararg(),
                authPromptConfig = any(),
                context = any()
            )
        ).thenReturn(returnResult)
    }
}
