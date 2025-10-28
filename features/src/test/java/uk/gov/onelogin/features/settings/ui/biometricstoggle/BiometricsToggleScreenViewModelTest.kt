package uk.gov.onelogin.features.settings.ui.biometricstoggle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepositoryImpl
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@RunWith(AndroidJUnit4::class)
class BiometricsToggleScreenViewModelTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var featureFlags: FeatureFlags
    private lateinit var localAuthPrefRepo: LocalAuthPreferenceRepo
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var navigator: Navigator
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var viewModel: BiometricsToggleScreenViewModel

    @Before
    fun setup() {
        featureFlags = mock()
        localAuthPrefRepo = LocalAuthPreferenceRepositoryImpl(context)
        deviceBiometricsManager = mock()
        analyticsLogger = mock()
        localAuthManager = LocalAuthManagerImpl(
            localAuthPrefRepo = localAuthPrefRepo,
            deviceBiometricsManager = deviceBiometricsManager,
            analyticsLogger = analyticsLogger
        )
        navigator = mock()
        autoInitialiseSecureStore = mock()
        viewModel = BiometricsToggleScreenViewModel(
            featureFlags = featureFlags,
            localAuthManager = localAuthManager,
            navigator = navigator,
            autoInitialiseSecureStore = autoInitialiseSecureStore
        )
    }

    @Test
    fun `test go back`() {
        viewModel.goBack()

        verify(navigator).goBack()
    }

    @Test
    fun `test checking biometrics available - ENABLED`() {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.NOT_ENROLLED)

        viewModel.checkBiometricsAvailable()

        verify(navigator).goBack()
    }

    @Test
    fun `test checking biometrics available - DISABLED`() {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.SUCCESS)

        viewModel.checkBiometricsAvailable()

        verify(navigator, times(0)).goBack()
    }

    @Test
    fun `test toggle biometrics - from biometrics ENABLED`() = runTest {
        localAuthPrefRepo.setLocalAuthPref(LocalAuthPreference.Enabled(true))
        whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)

        viewModel.toggleBiometrics()

        assertEquals(
            LocalAuthPreference.Enabled(false),
            localAuthManager.localAuthPreference
        )
        verify(autoInitialiseSecureStore, times(0)).initialise(null)
    }

    @Test
    fun `test toggle biometrics - from biometrics DISABLED (passcode)`() = runTest {
        localAuthPrefRepo.setLocalAuthPref(LocalAuthPreference.Enabled(false))
        whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.SUCCESS)

        viewModel.toggleBiometrics()

        assertEquals(
            LocalAuthPreference.Enabled(true),
            localAuthManager.localAuthPreference
        )
        verify(autoInitialiseSecureStore).initialise(null)
    }

    @Test
    fun `test toggle biometrics - from biometrics DISABLED (none)`() = runTest {
        localAuthPrefRepo.setLocalAuthPref(LocalAuthPreference.Disabled)
        whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.SUCCESS)

        viewModel.toggleBiometrics()

        assertEquals(
            LocalAuthPreference.Enabled(true),
            localAuthManager.localAuthPreference
        )
        verify(autoInitialiseSecureStore).initialise(null)
    }

    @Test
    fun `test wallet feature flag`() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)

        assertTrue(viewModel.walletEnabled)

        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)

        assertFalse(viewModel.walletEnabled)
    }
}
