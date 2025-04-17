package uk.gov.idcheck.network.api

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import uk.gov.idcheck.network.api.checker.OnlineChecker
import uk.gov.idcheck.network.api.checker.OnlineCheckerImpl
import uk.gov.idcheck.network.api.coroutines.AppCoroutineDispatchers
import uk.gov.idcheck.network.api.coroutines.DispatchersImpl
import uk.gov.idcheck.network.api.di.HttpClientBaseUri
import uk.gov.idcheck.network.api.usecases.GetAppInfoFromApi
import uk.gov.idcheck.network.api.usecases.GetAppInfoFromApiImpl
import uk.gov.idcheck.network.api.useragent.UserAgentGenerator
import uk.gov.onelogin.idcheck.network.api.R
import javax.inject.Singleton

object NetworkApiHiltConfig {
    @InstallIn(SingletonComponent::class)
    @Module
    object AppCoroutineSingletonModule {
        @Provides
        @Singleton
        fun providesAppCoroutineDispatchers(): AppCoroutineDispatchers = DispatchersImpl
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object AppInfoFetcherModule {
        @Provides
        @ViewModelScoped
        fun providesGetAppInfo(getAppInfoFromApi: GetAppInfoFromApiImpl): GetAppInfoFromApi =
            getAppInfoFromApi
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object HttpModule {
        @OptIn(ExperimentalSerializationApi::class)
        @Provides
        @Singleton
        fun providesHttpClient(userAgentGenerator: UserAgentGenerator): HttpClient {
            val simpleLogger = Logger.SIMPLE

            return HttpClient(Android) {
                expectSuccess = true

                install(UserAgent) {
                    agent = userAgentGenerator.getAgent()
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            explicitNulls = false
                        },
                    )
                }

                install(Logging) {
                    logger = simpleLogger
                    level = LogLevel.ALL
                }

                HttpResponseValidator {
                    handleResponseExceptionWithRequest { exception, _ ->
                        simpleLogger.log("Non-success response received: $exception")

                        val responseException =
                            exception as? ResponseException
                                ?: return@handleResponseExceptionWithRequest
                        val exceptionResponse = responseException.response

                        throw ResponseException(exceptionResponse, exceptionResponse.bodyAsText())
                    }
                }
            }
        }

        @Provides
        @Singleton
        @HttpClientBaseUri
        fun providesBaseUrl(
            @ApplicationContext context: Context,
        ): String = context.resources.getString(R.string.backendApiUrl)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkManagerSingletonModule {
        @Provides
        @Singleton
        fun providesNetworkManager(
            @ApplicationContext
            context: Context,
        ): ConnectivityManager =
            context.getSystemService(
                Context.CONNECTIVITY_SERVICE,
            ) as ConnectivityManager
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object OnlineCheckerModule {
        @Provides
        @Singleton
        fun providesOnlineChecker(checker: OnlineCheckerImpl): OnlineChecker = checker
    }
}
