package uk.gov.onelogin.developer.appcheck.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@SuppressWarnings("kotlin:S6517")
@Module
@InstallIn(ViewModelComponent::class)
interface AppCheckUseCaseModule {
    @Binds
    fun provideAssertionApiCall(
        useCase: AssertionApiCallImpl
    ): AssertionApiCall
}
