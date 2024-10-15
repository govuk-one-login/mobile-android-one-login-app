package uk.gov.onelogin.appinfo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.appinfo.apicall.data.AppInfoApiImpl
import uk.gov.onelogin.appinfo.apicall.domain.AppInfoApi
import uk.gov.onelogin.appinfo.service.data.AppInfoServiceImpl
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.source.data.AppInfoLocalSourceImpl
import uk.gov.onelogin.appinfo.source.data.AppInfoRemoteSourceImpl
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

@Module
@InstallIn(ViewModelComponent::class)
interface AppInfoApiModule {
    @Binds
    fun provideAppInfoApiCall(appInfoApiImpl: AppInfoApiImpl): AppInfoApi

    @Binds
    fun provideAppInfoRemoteSource(
        appInfoRemoteSource: AppInfoRemoteSourceImpl
    ): AppInfoRemoteSource

    @Binds
    fun provideAppInfoLocalSource(appInfoLocalSource: AppInfoLocalSourceImpl): AppInfoLocalSource

    @Binds
    fun provideAppInfoService(appInfoService: AppInfoServiceImpl): AppInfoService
}
