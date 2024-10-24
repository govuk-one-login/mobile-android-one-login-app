package uk.gov.onelogin.appinfo

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import uk.gov.android.onelogin.BuildConfig
import uk.gov.onelogin.appinfo.apicall.data.AppInfoApiImpl
import uk.gov.onelogin.appinfo.apicall.domain.AppInfoApi
import uk.gov.onelogin.appinfo.appversioncheck.data.AppVersionCheckImpl
import uk.gov.onelogin.appinfo.appversioncheck.domain.AppVersionCheck
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

    @Binds
    fun provideUtils(appInfoUtils: AppInfoUtilsImpl): AppInfoUtils

    @Binds
    fun provideAppVersionCheck(appVersionCheck: AppVersionCheckImpl): AppVersionCheck
}

@Module
@InstallIn(ViewModelComponent::class)
object BuildConfigProviderModule {
    @Provides
    @ViewModelScoped
    @BuildConfigVersion
    fun provideBuildConfigVersion(): String {
        return BuildConfig.VERSION_NAME
    }
}

annotation class BuildConfigVersion
