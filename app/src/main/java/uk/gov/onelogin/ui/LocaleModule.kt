package uk.gov.onelogin.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocaleModule {
    @Binds
    fun bindLocaleUtils(utils: LocaleUtilsImpl): LocaleUtils
}
