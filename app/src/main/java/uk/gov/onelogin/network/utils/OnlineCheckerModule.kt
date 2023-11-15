package uk.gov.onelogin.network.utils

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface OnlineCheckerModule {

    @Binds
    @Singleton
    fun bindsOnlineChecker(checker: OnlineChecker): IOnlineChecker
}
