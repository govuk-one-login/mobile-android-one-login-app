package uk.gov.onelogin.core.tokens.domain.retrieve

import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.android.securestore.error.SecureStoreErrorTypeV2
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.FragmentActivityTestCase
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

@RunWith(AndroidJUnit4::class)
class GetFromEncryptedSecureStoreTest : FragmentActivityTestCase() {
    private lateinit var useCase: GetFromEncryptedSecureStore
    private val mockSecureStore: SecureStoreAsyncV2 = mock()
    private val logger = SystemLogger()

    private val expectedStoreKey: String = "key"

    @Before
    fun setUp() {
        useCase = GetFromEncryptedSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun secureStoreFailsWithUserCancelled() =
        runTest {
            mockSecureStore(null, SecureStoreErrorTypeV2.USER_CANCELLED)

            assertTrue(logger.size == 0)

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
            ) {
                assertEquals(LocalAuthStatus.UserCancelledBioPrompt, it)
            }

            assertTrue(logger.size > 0)
        }

    @Test
    fun success() =
        runTest {
            val expectedKey = "expectedKey"
            val expectedValue = "expectedValue"
            val expectedResult = mapOf(expectedKey to expectedValue)
            mockSecureStore(expectedResult)

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
                "test",
            ) {
                assertEquals(LocalAuthStatus.Success(expectedResult), it)
            }

            assertEquals(0, logger.size)
        }

    private suspend fun mockSecureStore(
        returnResult: Map<String, String?>? = null,
        secureErrorType: SecureStoreErrorTypeV2? = null
    ) {
        returnResult?.let {
            whenever(
                mockSecureStore.retrieveWithAuthentication(
                    anyVararg(),
                    authPromptConfig = any(),
                    context = any(),
                ),
            ).thenReturn(returnResult)
        }
        secureErrorType?.let {
            whenever(
                mockSecureStore.retrieveWithAuthentication(
                    anyVararg(),
                    authPromptConfig = any(),
                    context = any(),
                ),
            ).thenThrow(SecureStorageErrorV2(Exception("Error"), secureErrorType))
        }
    }
}
