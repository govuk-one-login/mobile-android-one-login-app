package uk.gov.onelogin.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.network.usecase.HelloWorldApiCall
import uk.gov.onelogin.network.usecase.HelloWorldApiCallImpl

@Module
@InstallIn(ViewModelComponent::class)
fun interface NetworkUsecaseModule {
    @Binds
    fun bindHelloWorldApiCall(impl: HelloWorldApiCallImpl): HelloWorldApiCall
}
