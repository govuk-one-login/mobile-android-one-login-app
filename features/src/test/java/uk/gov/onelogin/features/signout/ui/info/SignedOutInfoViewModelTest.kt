package uk.gov.onelogin.features.signout.ui.info

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCaseImpl
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignedOutInfoViewModelTest {
    private val tokenRepository: TokenRepository = mock()
    private val navigator: Navigator = mock()
    private val getPersistentId: GetPersistentId = mock()
    private val signOutUseCase: SignOutUseCase = mock()
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepo = mock()
    private val deviceBiometricsManager: DeviceBiometricsManager = mock()
    private val analyticsLogger: AnalyticsLogger = mock()
    private val logger = SystemLogger()
    private val credentialChecker: LocalAuthManager = LocalAuthManagerImpl(
        localAuthPrefRepo = localAuthPreferenceRepo,
        deviceBiometricsManager = deviceBiometricsManager,
        analyticsLogger = analyticsLogger
    )
    private val localAuthPrefResetUseCase: LocalAuthPrefResetUseCase =
        LocalAuthPrefResetUseCaseImpl(
            localAuthPreferenceRepo,
            credentialChecker
        )

    private val viewModel by lazy {
        SignedOutInfoViewModel(
            navigator,
            tokenRepository,
            getPersistentId,
            signOutUseCase,
            localAuthPrefResetUseCase,
            logger
        )
    }

    @Test
    fun `reset tokens calls use case`() {
        viewModel.resetTokens()

        verify(tokenRepository).clearTokenResponse()
    }

    @Test
    fun `navigator has back stack, reauth is true`() {
        whenever(navigator.hasBackStack()).thenReturn(true)
        assertTrue(viewModel.shouldReAuth())
    }

    @Test
    fun `navigator has no back stack, reauth is false`() {
        whenever(navigator.hasBackStack()).thenReturn(false)
        assertFalse(viewModel.shouldReAuth())
    }

    @Test
    fun `sign out usecase is called when persistent id is null`() =
        runTest {
            var callback = false
            whenever(getPersistentId.invoke()).thenReturn(null)

            viewModel.checkPersistentId { callback = true }

            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
        }

    @Test
    fun `sign out usecase is called when persistent id is empty`() =
        runTest {
            var callback = false
            whenever(getPersistentId.invoke()).thenReturn("")

            viewModel.checkPersistentId { callback = true }

            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
        }

    @Test
    fun `sign out usecase is not called when persistent id good and device secure`() =
        runTest {
            var callback = false
            whenever(getPersistentId.invoke()).thenReturn("id")
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)

            viewModel.checkPersistentId { callback = true }

            verifyNoInteractions(signOutUseCase)
            verifyNoInteractions(navigator)
            assertTrue(callback)
        }

    @Test
    fun `sign out usecase is not called when persistent id good and device unsecure`() =
        runTest {
            var callback = false
            whenever(getPersistentId.invoke()).thenReturn("id")
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(false)

            viewModel.checkPersistentId { callback = true }

            verifyNoInteractions(signOutUseCase)
            verifyNoInteractions(navigator)
            verify(localAuthPreferenceRepo).clean()
            assertTrue(callback)
        }

    @Test
    fun `sign out usecase throws`() =
        runTest {
            var callback = false
            whenever(getPersistentId.invoke()).thenReturn("")
            whenever(signOutUseCase.invoke()).thenThrow(SignOutError(Error("test")))

            viewModel.checkPersistentId { callback = true }

            verify(signOutUseCase).invoke()
            verify(navigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertFalse(callback)
            assertTrue(logger.contains("java.lang.Error: test"))
        }
}
