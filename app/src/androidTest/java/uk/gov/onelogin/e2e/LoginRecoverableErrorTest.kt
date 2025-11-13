package uk.gov.onelogin.e2e

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.biometric.BiometricManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.intent.Intents
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import javax.inject.Named
import kotlin.io.encoding.ExperimentalEncodingApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.integrity.keymanager.ECKeyManager
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import uk.gov.android.authentication.integrity.model.AppIntegrityConfiguration
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.onelogin.core.R
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.HiltTestActivity
import uk.gov.onelogin.OneLoginApp
import uk.gov.onelogin.appcheck.AppCheckerModule
import uk.gov.onelogin.appinfo.AppInfoApiModule
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepositoryImpl
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.LocaleUtils
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.login.LoginSessionModule
import uk.gov.onelogin.login.VerifyIdModule
import uk.gov.onelogin.login.appintegrity.AppIntegrityModule
import uk.gov.onelogin.login.localauth.BiometricsModule
import uk.gov.onelogin.utils.TestUtils

@Suppress("SwallowedException")
@HiltAndroidTest
@UninstallModules(
    LoginSessionModule::class,
    BiometricsModule::class,
    AppInfoApiModule::class,
    AppCheckerModule::class,
    AppIntegrityModule::class,
    VerifyIdModule::class
)
class LoginRecoverableErrorTest : TestCase() {
    @BindValue
    val mockLoginSession: LoginSession = mock()

    @BindValue
    lateinit var mockDeviceBiometricManager: DeviceBiometricsManager

    @BindValue
    lateinit var mockBiometricManager: BiometricManager

    @BindValue
    val mockAppInfoService: AppInfoService = mock()

    @BindValue
    val mockAppInfoLocalSource: AppInfoLocalSource = mock()

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var localeUtils: LocaleUtils

    @Inject
    lateinit var navigator: Navigator

    @BindValue
    lateinit var mockLocalAuthRepo: LocalAuthPreferenceRepo

    @BindValue
    val mockAppIntegrity: AppIntegrity = mock()

    @BindValue
    val mockAttestationManager: AppIntegrityManager = mock()

    @BindValue
    val mockAttestationCaller: AttestationCaller = mock()

    @BindValue
    val mockAppChecker: AppChecker = mock()

    @OptIn(ExperimentalEncodingApi::class)
    @BindValue
    val mockKeyStoreManager: KeyStoreManager = ECKeyManager()

    @BindValue
    val mockAppIntegrityConfiguration: AppIntegrityConfiguration = AppIntegrityConfiguration(
        mockAttestationCaller,
        mockAppChecker,
        mockKeyStoreManager
    )

    @Inject
    @Named("Open")
    lateinit var secureStore: SecureStore

    @BindValue
    val mockVerifyIdToken: VerifyIdToken = mock()

    private val appInfoData = TestUtils.appInfoData

    @get:Rule(order = 4)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        Intents.init()
        mockDeviceBiometricManager = mock()
        mockBiometricManager = mock()
        mockLocalAuthRepo = LocalAuthPreferenceRepositoryImpl(context)
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })
        hiltRule.inject()
        deletePersistentId()
    }

    @After
    fun tearDown() {
        Intents.release()
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun handleActivityResultWithDataButLoginThrowsRecoverableError() {
        val authenticationError = AuthenticationError(
            message = "Error",
            type = AuthenticationError.ErrorType.SERVER_ERROR
        )
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[4] as (Throwable) -> Unit).invoke(authenticationError)
        }
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )
        clickOptOut()
        composeRule.onNodeWithText(resources.getString(R.string.app_signInButton)).performClick()
        composeRule.waitUntil(5000) {
            composeRule.onNodeWithText("Try to sign in again.").isDisplayed()
        }
    }

    private fun setupActivityForResult(
        returnedIntent: Intent?,
        resultCode: Int = Activity.RESULT_OK
    ) {
        whenever(mockLoginSession.present(any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[0] as ActivityResultLauncher<Intent>).launch(Intent())
        }

        composeRule.setContent {
            val registryOwner = object : ActivityResultRegistryOwner {
                override val activityResultRegistry: ActivityResultRegistry
                    get() = object : ActivityResultRegistry() {
                        override fun <I : Any?, O : Any?> onLaunch(
                            requestCode: Int,
                            contract: ActivityResultContract<I, O>,
                            input: I,
                            options: ActivityOptionsCompat?
                        ) {
                            this.dispatchResult(requestCode, resultCode, returnedIntent)
                        }
                    }
            }
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                val navController = rememberNavController()

                DisposableEffect(key1 = navController) {
                    navigator.setController(navController)

                    onDispose {
                        navigator.reset()
                    }
                }

                OneLoginApp(navController = navController)
            }
        }
    }

    private fun clickOptOut() {
        composeRule.waitForIdle()
        val doNotShare = composeRule.onNodeWithText(
            resources.getString(R.string.app_doNotShareAnalytics)
        )
        val isOnOptInScreen = doNotShare.isDisplayed()
        if (isOnOptInScreen) {
            doNotShare.performClick()
        }
    }

    private fun deletePersistentId() {
        secureStore.delete(
            key = AuthTokenStoreKeys.PERSISTENT_ID_KEY
        )
    }
}
