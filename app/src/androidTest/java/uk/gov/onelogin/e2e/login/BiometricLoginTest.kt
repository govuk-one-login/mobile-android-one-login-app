package uk.gov.onelogin.e2e.login

import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Named
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.credentialchecker.BiometricManager
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.credentialchecker.CredentialCheckerModule
import uk.gov.onelogin.e2e.selectors.BySelectors.bioOptInTitle
import uk.gov.onelogin.e2e.selectors.BySelectors.enableBiometricsButton
import uk.gov.onelogin.e2e.selectors.BySelectors.homeTitle
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.SecureStoreModule
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@HiltAndroidTest
@UninstallModules(CredentialCheckerModule::class, SecureStoreModule::class)
class BiometricLoginTest : BaseLoginTest() {
    @BindValue
    val mockCredChecker: CredentialChecker = mock()

    @BindValue
    val mockBiometricManager: BiometricManager = mock()

    @BindValue
    @Named("Open")
    val secureStoreOpen: SecureStore = mock()

    @BindValue
    @Named("Token")
    val secureStoreToken: SecureStore = mock()

    @BindValue
    val autoInitialiseSecureStore: AutoInitialiseSecureStore = mock()

    @Test
    fun testRun() {
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        runBlocking {
            whenever(secureStoreOpen.retrieve(Keys.PERSISTENT_ID_KEY)).thenReturn(
                RetrievalEvent.Failed(
                    SecureStoreErrorType.GENERAL,
                    "No persistentId set"
                )
            )
        }

        phoneController.apply {
            startApp()
            goodLogin()

            phoneController.assertElementExists(
                WAIT_FOR_OBJECT_TIMEOUT,
                bioOptInTitle(context)
            )
            phoneController.click(
                WAIT_FOR_OBJECT_TIMEOUT,
                enableBiometricsButton(context) to "Enable biometrics button"
            )
            phoneController.assertElementExists(
                WAIT_FOR_OBJECT_TIMEOUT,
                homeTitle(context)
            )
        }
    }
}
