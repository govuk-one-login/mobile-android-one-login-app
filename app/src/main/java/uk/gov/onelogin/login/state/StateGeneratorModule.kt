package uk.gov.onelogin.login.state

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StateGeneratorModule {

    @Provides
    @Singleton
    fun bindsStateGenerator(stateGenerator: StateGenerator): IStateGenerator = stateGenerator
}
