package uk.gov.onelogin.criorchestrator

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.criorchestrator.features.config.publicapi.Config
import uk.gov.onelogin.criorchestrator.features.config.publicapi.SdkConfigKey
import uk.gov.onelogin.criorchestrator.sdk.publicapi.CriOrchestratorSdkExt.create
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

@Module
@InstallIn(SingletonComponent::class)
object CriOrchestratorModule {
    @Provides
    @Singleton
    fun provideSdkConfig(
        @ApplicationContext context: Context
    ) = Config(
        entries =
        persistentListOf(
            Config.Entry(
                key = SdkConfigKey.IdCheckAsyncBackendBaseUrl,
                Config.Value.StringValue(
                    value = context.getString(
                        uk.gov.android.onelogin.core.R.string.backendAsyncUrl
                    )
                )
            )
        )
    )

    @Provides
    @Singleton
    fun provideCriOrchestratorSdk(
        @ApplicationContext context: Context,
        genericHttpClient: GenericHttpClient,
        analyticsLogger: AnalyticsLogger,
        sdkConfig: Config,
        logger: Logger
    ): CriOrchestratorSdk {
        return CriOrchestratorSdk.create(
            genericHttpClient,
            analyticsLogger,
            sdkConfig,
            logger,
            context
        )
    }
}
