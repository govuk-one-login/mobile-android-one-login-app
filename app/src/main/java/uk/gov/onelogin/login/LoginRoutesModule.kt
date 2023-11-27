package uk.gov.onelogin.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginRoutesModule {

    @Provides
    @Singleton
    fun providesLoginRoutes(loginRoutes: LoginRoutes): ILoginRoutes = loginRoutes
}
