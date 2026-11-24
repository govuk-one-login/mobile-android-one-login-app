package uk.gov.onelogin.criorchestrator

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.collections.immutable.persistentListOf
import uk.gov.onelogin.criorchestrator.features.config.publicapi.Config
import uk.gov.onelogin.criorchestrator.features.config.publicapi.SdkConfigKey
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.criorchestrator.CheckIdCheckSessionState
import uk.gov.onelogin.features.criorchestrator.CheckIdCheckSessionStateImpl
import uk.gov.onelogin.libraries.mockcriorchestratorsdk.MockCriOrchestratorSdk

/**
 * Provides a mocked [CriOrchestratorSdk] in place of the real ID Check SDK, so no ID Check SDK
 * is instantiated.
 */
@Module
@InstallIn(SingletonComponent::class)
object CriOrchestratorModule {
    /**
     * The mocked SDK ignores this, as the config is only an input to creating the real SDK. It is
     * still provided because the shared instrumentation test `CriOrchestratorModuleTest` injects
     * it for every flavour.
     */
    @Provides
    @Singleton
    fun provideSdkConfig(@ApplicationContext context: Context) = Config(
        entries =
            persistentListOf(
                Config.Entry(
                    key = SdkConfigKey.IdCheckAsyncBackendBaseUrl,
                    Config.Value.StringValue(
                        value =
                            context.getString(
                                uk.gov.android.onelogin.core.R.string.backendAsyncUrl,
                            ),
                    ),
                ),
            ),
    )

    @Provides
    @Singleton
    fun provideCriOrchestratorSdk(): CriOrchestratorSdk = MockCriOrchestratorSdk()

    @Provides
    @Singleton
    fun provideCheckIdCheckIsActive(
        criOrchestratorSdk: CriOrchestratorSdk,
    ): CheckIdCheckSessionState = CheckIdCheckSessionStateImpl(criOrchestratorSdk)
}
