package uk.gov.onelogin.appCheck.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.appcheck.usecase.AssertionApiCall
import uk.gov.onelogin.appcheck.usecase.AssertionApiCallImpl

@Module
@InstallIn(ViewModelComponent::class)
interface AppCheckUseCaseModule {
    @Binds
    fun provideAssertionApiCall(
        useCase: AssertionApiCallImpl
    ): AssertionApiCall
}
