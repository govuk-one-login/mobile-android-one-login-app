package uk.gov.onelogin.features.settings.ui.biomtericsoptin

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.settings.ui.biometricsoptin.BiometricsOptInScreenViewModel

class BiometricsOptInScreenViewModelTest {
    private lateinit var featureFlags: FeatureFlags
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var navigator: Navigator
    private lateinit var viewModel: BiometricsOptInScreenViewModel

    @Before
    fun setup() {
        featureFlags = mock()
        localAuthManager = mock()
        navigator = mock()
        viewModel = BiometricsOptInScreenViewModel(featureFlags, localAuthManager, navigator)
    }

    @Test
    fun `test go back`() {
        viewModel.goBack()

        verify(navigator).goBack()
    }

    @Test
    fun `test checking biometrics available - ENABLED`() {
        whenever(localAuthManager.biometricsAvailable()).thenReturn(false)

        viewModel.checkBiometricsAvailable()

        verify(navigator).goBack()
    }

    @Test
    fun `test checking biometrics available - DISABLED`() {
        whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

        viewModel.checkBiometricsAvailable()

        verify(navigator, times(0)).goBack()
    }

    @Test
    fun `test toggle biometrics - from biometrics ENABLED`() = runTest {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(true))

        viewModel.toggleBiometrics()

        verify(localAuthManager).toggleBiometrics()
    }

    @Test
    fun `test toggle biometrics - from biometrics DISABLED (passcode)`() = runTest {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        viewModel.toggleBiometrics()

        verify(localAuthManager).toggleBiometrics()
    }

    @Test
    fun `test toggle biometrics - from biometrics DISABLED (none)`() {
        whenever(localAuthManager.localAuthPreference)
            .thenReturn(LocalAuthPreference.Disabled)
        viewModel.toggleBiometrics()

        verify(localAuthManager).toggleBiometrics()
    }

    @Test
    fun `test wallet feature flag`() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)

        assertTrue(viewModel.walletEnabled)

        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)

        assertFalse(viewModel.walletEnabled)
    }
}
