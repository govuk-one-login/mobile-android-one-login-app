package uk.gov.onelogin.e2e

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.biometric.BiometricManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.filters.FlakyTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import javax.inject.Named
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.integrity.keymanager.ECKeyManager
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import uk.gov.android.authentication.integrity.model.AppIntegrityConfiguration
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.localauth.R as LocalAuthR
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus
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
import uk.gov.onelogin.utils.RetryableComposeTestRule
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
class LoginTest : TestCase() {
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

    // Remove this once Secure Store is fixed
    private val sharedPrefs = context.getSharedPreferences("SharedPrefs.key", Context.MODE_PRIVATE)

    private val appInfoData = TestUtils.appInfoData

    // use createEmptyComposeRule instead of createAndroidComposeRule<HiltTestActivity>() to avoid
    // IllegalStateException caused by composeRule.setContent being called twice
    @get:Rule(order = 3)
    val composeRule = RetryableComposeTestRule()

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    private var injectCounter = 0

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(HiltTestActivity::class.java)
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
        hiltInject()
        deletePersistentId()
    }

    @After
    fun tearDown() {
        scenario.close()
        Intents.release()
        ArchTaskExecutor.getInstance().setDelegate(null)
        injectCounter = 0
    }

    @Test
    @FlakyTest
    fun selectingLoginButtonFiresAuthRequestNoPersistentId() {
        runBlocking {
            whenever(mockAppInfoService.get()).thenReturn(
                AppInfoServiceState.Successful(appInfoData)
            )
            whenever(mockAppIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("Success"))
        }

        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        wheneverBlocking { mockVerifyIdToken.invoke(any(), any()) }.thenReturn(true)
        tokenRepository.setTokenResponse(
            TokenResponse(
                tokenType = "type",
                accessToken = "access",
                accessTokenExpirationTime = 1L,
                idToken = ""
            )
        )

        startApp()

        composeRule.apply {
            onNodeWithText(context.getString(R.string.app_SignInWithGovUKOneLoginButton))
                .clickIfExisting()
            onNodeWithText(context.getString(R.string.app_dataDeletedButton))
                .clickIfExisting()
        }

        clickOptOut()
        clickLogin()

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.stsUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(
                    R.string.tokenExchangeEndpoint
                )
            )
        )
        val loginConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeUrl,
            clientId = resources.getString(R.string.stsClientId),
            locale = localeUtils.getLocaleAsSessionConfig(),
            redirectUri = redirectUrl,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = null
        )

        verify(mockLoginSession, atLeast(1)).present(any(), eq(loginConfig))
    }

    @Test
    fun selectingLoginButtonFiresAuthRequestWithPersistentIdFromSecureStore() = runTest {
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))

        startApp()
        clickOptOut()
        setPersistentId()
        clickLogin()

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.stsUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(
                    R.string.tokenExchangeEndpoint
                )
            )
        )
        val loginConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeUrl,
            clientId = resources.getString(R.string.stsClientId),
            locale = localeUtils.getLocaleAsSessionConfig(),
            redirectUri = redirectUrl,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = PERSISTENT_ID
        )

        verify(mockLoginSession).present(any(), eq(loginConfig))
    }

    // App remains on sign in page when not data is returned in intent from login
    @Test
    fun handleActivityResultNullData() {
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        setupActivityForResult(
            null
        )

        clickOptOut()
        clickLogin()

        composeRule.waitForIdle()
        nodeWithTextExists(resources.getString(R.string.app_signInTitle))
        verify(mockLoginSession, times(0)).finalise(any(), any(), any(), any(), any())
    }

    @Test
    fun handleActivityCancelledResult() {
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        setupActivityForResult(Intent(), resultCode = Activity.RESULT_CANCELED)

        clickOptOut()
        clickLogin()

        nodeWithTextExists(resources.getString(R.string.app_signInTitle))
        verify(mockLoginSession, times(0)).finalise(any(), any(), any(), any(), any())
    }

    @Test
    @FlakyTest
    fun handleActivityResultWithDataButLoginThrowsUnrecoverableError() {
        val authenticationError = AuthenticationError(
            message = "Error",
            type = AuthenticationError.ErrorType.TOKEN_ERROR
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
        nodeWithTextExists("Try again later.")
    }

    @Test
    @FlakyTest
    fun handleActivityResultWithDataUnsecured() {
        deletePersistentId()
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        whenever(mockDeviceBiometricManager.isDeviceSecure()).thenReturn(false)
        wheneverBlocking { mockVerifyIdToken.invoke(any(), any()) }.thenReturn(true)
        mockGoodLogin()
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )

        composeRule.apply {
            onNodeWithText(context.getString(R.string.app_SignInWithGovUKOneLoginButton))
                .clickIfExisting()
            onNodeWithText(context.getString(R.string.app_dataDeletedButton))
                .clickIfExisting()
        }

        clickOptOut()
        clickLogin()

        composeRule.onNodeWithTag(
            resources.getString(R.string.welcomeCardTestTag)
        ).isDisplayed()
    }

    @Test
    fun handleActivityResultWithDataBioOptIn() {
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        whenever(mockDeviceBiometricManager.isDeviceSecure()).thenReturn(true)
        whenever(mockDeviceBiometricManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.SUCCESS)
        wheneverBlocking { mockVerifyIdToken.invoke(any(), any()) }.thenReturn(true)
        mockGoodLogin()
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )
        clickOptOut()
        clickLogin()

        composeRule.apply {
            nodeWithTextExists(resources.getString(LocalAuthR.string.app_enableBiometricsTitle))

            onNodeWithText(
                resources.getString(LocalAuthR.string.app_enablePasscodeOrPatternButton)
            ).performClick()

            onNodeWithTag(
                resources.getString(R.string.welcomeCardTestTag)
            ).isDisplayed()
        }
    }

    @Test
    @FlakyTest
    fun handleActivityResultWithDataPasscode() {
        deletePersistentId()
        wheneverBlocking { mockAppInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(appInfoData))
        wheneverBlocking { mockAppIntegrity.getClientAttestation() }
            .thenReturn(AttestationResult.Success("Success"))
        whenever(mockAppIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("Success"))
        whenever(mockDeviceBiometricManager.isDeviceSecure()).thenReturn(true)
        whenever(mockDeviceBiometricManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.NOT_ENROLLED)
        wheneverBlocking { mockVerifyIdToken.invoke(any(), any()) }.thenReturn(true)
        mockGoodLogin()
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )

        composeRule.apply {
            onNodeWithText(context.getString(R.string.app_SignInWithGovUKOneLoginButton))
                .clickIfExisting()
            onNodeWithText(context.getString(R.string.app_dataDeletedButton))
                .clickIfExisting()
        }

        clickOptOut()
        clickLogin()

        composeRule.onNodeWithTag(
            resources.getString(R.string.welcomeCardTestTag)
        ).isDisplayed()
    }

    private fun setupActivityForResult(
        returnedIntent: Intent?,
        resultCode: Int = Activity.RESULT_OK
    ) {
        whenever(mockLoginSession.present(any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[0] as ActivityResultLauncher<Intent>).launch(Intent())
        }

        scenario.onActivity { activity ->
            activity.setContent {
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
    }

    private fun startApp() {
        scenario.onActivity { activity ->
            activity.setContent {
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

    private fun clickLogin() {
        composeRule.waitUntil(TIMEOUT) {
            composeRule.onNodeWithText(resources.getString(R.string.app_signInButton))
                .isDisplayed()
        }
        composeRule.onNodeWithText(resources.getString(R.string.app_signInButton)).performClick()
    }

    private fun nodeWithTextExists(text: String) {
        composeRule.waitUntil(5000) {
            composeRule.onNodeWithText(text).isDisplayed()
        }
    }

    private fun SemanticsNodeInteraction.clickIfExisting() {
        try {
            assertIsDisplayed()
            performClick()
        } catch (e: AssertionError) {
            // Nothing to do, just let it continue if this does not exist
        }
    }

    private fun mockGoodLogin() {
        whenever(mockLoginSession.finalise(any(), any(), any(), any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[3] as (TokenResponse) -> Unit).invoke(tokenResponse)
        }
    }

    private suspend fun setPersistentId() {
        secureStore.upsert(
            key = AuthTokenStoreKeys.PERSISTENT_ID_KEY,
            value = PERSISTENT_ID
        )
        sharedPrefs.edit().putString(AuthTokenStoreKeys.PERSISTENT_ID_KEY, PERSISTENT_ID).apply()
    }

    private fun deletePersistentId() {
        secureStore.delete(
            key = AuthTokenStoreKeys.PERSISTENT_ID_KEY
        )
    }

    private fun hiltInject() {
        if (injectCounter < 1) {
            hiltRule.inject()
        }
        injectCounter++
    }

    companion object {
        const val TIMEOUT = 10000L
        private const val PERSISTENT_ID = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
        private val tokenResponse = TokenResponse(
            tokenType = "test",
            accessToken = "test",
            accessTokenExpirationTime = 1L,
            idToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN" +
                "2Q5LTk4NzgxZWJkZjkzZCJ9.eyJhdWQiOiJHRVV6a0V6SVFVOXJmYmdBWmJzal9fMUVOUU0iLCJ" +
                "pc3MiOiJodHRwczovL3Rva2VuLmJ1aWxkLmFjY291bnQuZ292LnVrIiwic3ViIjoiOWQwZjIxZG" +
                "UtMmZkNy00MjdiLWE2ZGYtMDdjZDBkOTVlM2I2IiwicGVyc2lzdGVudF9pZCI6ImNjODkzZWNlL" +
                "WI2YmQtNDQ0ZC05YmI0LWRlYzZmNTc3OGU1MCIsImlhdCI6MTcyMTk5ODE3OCwiZXhwIjoxNzIx" +
                "OTk4MzU4LCJub25jZSI6InRlc3Rfbm9uY2UiLCJlbWFpbCI6Im1vY2tAZW1haWwuY29tIiwiZW1" +
                "haWxfdmVyaWZpZWQiOnRydWV9.G1uQ9z2i-214kEmmtK7hEHRsgqJdk7AXjz_CaJDiuuqSyHZ4W" +
                "48oE1karDBA-pKWpADdBpHeUC-eCjjfBObjOg",
            refreshToken = null
        )
    }
}
