package uk.gov.idcheck.iproov.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import uk.gov.idcheck.iproov.api.usecases.FinishIProovActivity
import uk.gov.idcheck.iproov.api.usecases.FinishIProovActivityImpl
import uk.gov.idcheck.iproov.api.usecases.GetIproovTokenFromReadId
import uk.gov.idcheck.iproov.api.usecases.GetIproovTokenFromReadIdImpl
import uk.gov.idcheck.iproov.api.usecases.RegisterListener
import uk.gov.idcheck.iproov.api.usecases.RegisterListenerImpl
import uk.gov.idcheck.iproov.api.usecases.UnregisterListener
import uk.gov.idcheck.iproov.api.usecases.UnregisterListenerImpl

object IproovApiHiltConfig {
    @Module
    @InstallIn(ViewModelComponent::class)
    object FinishIProovActivityViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesFinishIProovActivity(
            finishIProovActivityImpl: FinishIProovActivityImpl,
        ): FinishIProovActivity = finishIProovActivityImpl
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object GetIProovTokenFromReadIdViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesGetIproovTokenFromReadId(
            getIproovTokenFromReadId: GetIproovTokenFromReadIdImpl,
        ): GetIproovTokenFromReadId = getIproovTokenFromReadId
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object RegisterListenerViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesRegisterListener(registerListener: RegisterListenerImpl): RegisterListener =
            registerListener
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object UnregisterListenerViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesUnregisterListener(
            unregisterListener: UnregisterListenerImpl,
        ): UnregisterListener = unregisterListener
    }
}
