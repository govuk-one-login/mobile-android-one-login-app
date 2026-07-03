package uk.gov.onelogin.core.tokens.domain.retrieve

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.android.securestore.error.SecureStoreErrorTypeV2
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.FragmentActivityTestCase
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

@RunWith(RobolectricTestRunner::class)
class GetFromEncryptedSecureStoreTest : FragmentActivityTestCase() {
    private lateinit var useCase: GetFromEncryptedSecureStore
    private val mockSecureStore: SecureStoreAsyncV2 = mock()
    private val logger = MemorisedLogger()

    private val expectedStoreKey: String = "key"

    @Before
    fun setUp() {
        useCase = GetFromEncryptedSecureStoreImpl(mockSecureStore, logger)
    }

    @Test
    fun `secure store fails with user cancelled`() =
        runTest {
            mockSecureStore(null, SecureStoreErrorTypeV2.USER_CANCELLED)

            assertThat(logger, hasSize(0))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
            ) {
                assertEquals(LocalAuthStatus.UserCancelledBioPrompt, it)
            }

            assertThat(logger, hasSize(greaterThan(0)))
        }

    @Test
    fun `secure store fails with unrecoverable error`() =
        runTest {
            mockSecureStore(null, SecureStoreErrorTypeV2.UNRECOVERABLE)

            assertThat(logger, hasSize(0))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
            ) {
                assertEquals(LocalAuthStatus.ReauthRequired, it)
            }

            assertThat(logger, hasSize(greaterThan(0)))
        }

    @Test
    fun `secure store fails with recoverable`() =
        runTest {
            mockSecureStore(null, SecureStoreErrorTypeV2.RECOVERABLE)

            assertThat(logger, hasSize(0))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
            ) {
                assertEquals(LocalAuthStatus.UserCancelledBioPrompt, it)
            }

            assertThat(logger, hasSize(greaterThan(0)))
        }

    @Test
    fun `secure store fails with no local auth enabled`() =
        runTest {
            mockSecureStore(null, SecureStoreErrorTypeV2.NO_LOCAL_AUTH_ENABLED)

            assertThat(logger, hasSize(0))

            useCase.invoke(
                composeTestRule.activity as FragmentActivity,
                expectedStoreKey,
            ) {
                assertEquals(LocalAuthStatus.UnrecoverableError, it)
            }

            assertThat(logger, hasSize(greaterThan(0)))
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

            assertThat(logger, hasSize(0))
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
