package uk.gov.onelogin.appcheck.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AppCheckUseCaseModule {
    @Binds
    fun provideAssertionApiCall(
        useCase: AssertionApiCallImpl
    ): AssertionApiCall
}
