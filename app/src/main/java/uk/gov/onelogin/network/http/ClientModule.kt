package uk.gov.onelogin.network.http

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ClientModule {

    @Binds
    @Singleton
    fun bindsHttpClient(client: HttpClient): IHttpClient
}
