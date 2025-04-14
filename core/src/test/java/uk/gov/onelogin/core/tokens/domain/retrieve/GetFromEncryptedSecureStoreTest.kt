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
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.FragmentActivityTestCase
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

@RunWith(AndroidJUnit4::class)
class GetFromEncryptedSecureStoreTest : FragmentActivityTestCase() {
    private lateinit var useCase: GetFromEncryptedSecureStore
    private val mockSecureStore: SecureStore = mock()
    private val logger = SystemLogger()

    private val expectedStoreKey: String = "key"

    @Before
    fun setUp() {
        useCase = GetFromEncryptedSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun secureStoreFailsWithGeneral() =
        runTest {
            mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.GENERAL))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey
            ) {
                assertEquals(LocalAuthStatus.SecureStoreError, it)
            }

            assertTrue(
                logger.contains(
                    "Secure store retrieval failed: " +
                        "\ntype - GENERAL\nreason - null"
                )
            )
        }

    @Test
    fun secureStoreFailsWithUserCancelled() =
        runTest {
            mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.USER_CANCELED_BIO_PROMPT))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey
            ) {
                assertEquals(LocalAuthStatus.UserCancelled, it)
            }

            assertTrue(
                logger.contains(
                    "Secure store retrieval failed: " +
                        "\ntype - USER_CANCELED_BIO_PROMPT\nreason - null"
                )
            )
        }

    @Test
    fun secureStoreFailsWithBioFailed() =
        runTest {
            mockSecureStore(RetrievalEvent.Failed(SecureStoreErrorType.FAILED_BIO_PROMPT))
            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey
            ) {
                assertEquals(LocalAuthStatus.BioCheckFailed, it)
            }

            assertTrue(
                logger.contains(
                    "Secure store retrieval failed: " +
                        "\ntype - FAILED_BIO_PROMPT\nreason - null"
                )
            )
        }

    @Test
    fun success() =
        runTest {
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

            assertEquals(0, logger.size)
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
