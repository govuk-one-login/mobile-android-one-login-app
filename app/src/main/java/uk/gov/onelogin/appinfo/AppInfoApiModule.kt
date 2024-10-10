package uk.gov.onelogin.appinfo

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.appinfo.apicall.data.AppInfoApiImpl
import uk.gov.onelogin.appinfo.apicall.domain.AppInfoApi
import uk.gov.onelogin.appinfo.service.data.AppInfoServiceImpl
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.source.data.AppInfoLocalSourceImpl
import uk.gov.onelogin.appinfo.source.data.AppInfoRemoteSourceImpl
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
interface AppInfoApiModule {
    @Binds
    fun provideAppInfoApiCall(appInfoApiImpl: AppInfoApiImpl): AppInfoApi

    @Binds
    fun provideAppInfoRemoteSource(appInfoRemoteSource: AppInfoRemoteSourceImpl): AppInfoRemoteSource

    @Binds
    fun provideAppInfoLocalSource(appInfoLocalSource: AppInfoLocalSourceImpl): AppInfoLocalSource

    @Binds
    fun provideAppInfoService(appInfoService: AppInfoServiceImpl): AppInfoService
}
