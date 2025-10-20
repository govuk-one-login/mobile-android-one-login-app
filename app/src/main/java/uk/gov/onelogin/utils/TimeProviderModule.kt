package uk.gov.onelogin.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.utils.SystemTimeProvider
import uk.gov.onelogin.core.utils.TimeProvider

@Module
@InstallIn(SingletonComponent::class)
object TimeProviderModule {
    @Provides
    fun provideSystemTimeProvider(): TimeProvider = SystemTimeProvider()
}
