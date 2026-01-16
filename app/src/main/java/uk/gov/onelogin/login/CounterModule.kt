package uk.gov.onelogin.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.counter.CounterImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CounterModule {
    @Provides
    @Singleton
    fun provideCounter(): Counter = CounterImpl()
}
