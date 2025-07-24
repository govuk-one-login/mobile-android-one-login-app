package uk.gov.onelogin.features.settings.ui

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetEmail
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.optin.data.OptInRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenViewModelTest {
    private lateinit var viewModel: SettingsScreenViewModel

    private lateinit var featureFlags: FeatureFlags
    private val mockNavigator: Navigator = mock()
    private val mockGetEmail: GetEmail = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockOptInRepository: OptInRepository = mock()
    private val mockLocalAuthManager: LocalAuthManager = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(false))
        featureFlags = InMemoryFeatureFlags(
            emptySet()
        )
        viewModel =
            SettingsScreenViewModel(
                mockOptInRepository,
                mockNavigator,
                mockLocalAuthManager,
                featureFlags,
                mockTokenRepository,
                mockGetEmail
            )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email is empty when getEmail returns null`() {
        whenever(mockGetEmail.invoke(any())).thenReturn(null)
        setup()

        assert(viewModel.email.isEmpty())
    }

    @Test
    fun `email is given when getEmail returns a value`() {
        whenever(mockGetEmail.invoke(any())).thenReturn("test")
        setup()

        assertEquals("test", viewModel.email)
    }

    @Test
    fun `goToSignOut() correctly navigates to sign out`() {
        viewModel.goToSignOut()

        verify(mockNavigator).navigate(SignOutRoutes.Start, false)
    }

    @Test
    fun `goToOssl() correctly navigates to open source licence page`() {
        viewModel.goToOssl()

        verify(mockNavigator).navigate(SettingsRoutes.Ossl, false)
    }

    @Test
    fun `optInState is false when repository returns false`() =
        runTest {
            whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(false))

            assertEquals(false, viewModel.optInState.value)
        }

    @Test
    fun `optInState is true when repository returns true`() =
        runTest {
            whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(true))
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )
            assertEquals(true, viewModel.optInState.value)
        }

    @Test
    fun `toggleOptInPreference calls optOut on repository when state is true`() =
        runTest {
            whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(true))
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )
            assertEquals(true, viewModel.optInState.value)

            viewModel.toggleOptInPreference()

            verify(mockOptInRepository).optOut()
        }

    @Test
    fun `toggleOptInPreference calls optOut on repository when state is false`() =
        runTest {
            whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(false))
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )
            assertEquals(false, viewModel.optInState.value)

            viewModel.toggleOptInPreference()

            verify(mockOptInRepository).optIn()
        }

    @Test
    fun `biometrics are not available`() =
        runTest {
            whenever(mockOptInRepository.hasAnalyticsOptIn()).thenReturn(flowOf(false))
            whenever(mockLocalAuthManager.biometricsAvailable())
                .thenReturn(false)
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )

            assertFalse(viewModel.biometricsOptionState.value)

            viewModel.checkDeviceBiometricsStatus()

            assertFalse(viewModel.biometricsOptionState.value)
        }

    @Test
    fun `biometrics are available`() =
        runTest {
            whenever(mockLocalAuthManager.biometricsAvailable())
                .thenReturn(true)
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )

            assertFalse(viewModel.biometricsOptionState.value)

            viewModel.checkDeviceBiometricsStatus()

            assertTrue(viewModel.biometricsOptionState.value)
        }

    @Test
    fun `wallet enabled`() =
        runTest {
            featureFlags = InMemoryFeatureFlags(
                setOf(WalletFeatureFlag.ENABLED)
            )
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )

            assertTrue(viewModel.isWalletEnabled)
        }

    @Test
    fun `wallet disabled`() =
        runTest {
            viewModel =
                SettingsScreenViewModel(
                    mockOptInRepository,
                    mockNavigator,
                    mockLocalAuthManager,
                    featureFlags,
                    mockTokenRepository,
                    mockGetEmail
                )

            assertFalse(viewModel.isWalletEnabled)
        }
}
