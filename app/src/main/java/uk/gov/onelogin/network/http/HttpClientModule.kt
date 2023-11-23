package uk.gov.onelogin.network.http

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface HttpClientModule {

    @Binds
    @Singleton
    fun bindsHttpClient(httpClient: HttpClient): IHttpClient
}
