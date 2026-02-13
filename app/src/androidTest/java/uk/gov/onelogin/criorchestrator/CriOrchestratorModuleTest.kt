package uk.gov.onelogin.criorchestrator

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.criorchestrator.features.config.publicapi.Config
import uk.gov.onelogin.criorchestrator.features.config.publicapi.SdkConfigKey
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.IdCheckWrapperConfigKey
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.nfc.NfcConfigKey
import uk.gov.onelogin.utils.TestCase
import javax.inject.Inject

@HiltAndroidTest
class CriOrchestratorModuleTest : TestCase() {
    @Inject
    lateinit var criConfig: Config

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun configIsCorrect() {
        assertFalse(criConfig.keys.contains(IdCheckWrapperConfigKey.EnableManualLauncher))
        assertFalse(criConfig.keys.contains(SdkConfigKey.DebugAppReviewPrompts))
        assertFalse(criConfig.keys.contains(SdkConfigKey.BypassIdCheckAsyncBackend))
        assertFalse(criConfig.keys.contains(NfcConfigKey.NfcAvailability))
        assertTrue(criConfig.keys.contains(SdkConfigKey.IdCheckAsyncBackendBaseUrl))
    }
}
