package uk.gov.idcheck.iproov.testdouble

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import uk.gov.idcheck.iproov.api.FaceScanner
import uk.gov.idcheck.iproov.api.usecases.LaunchFaceScan
import uk.gov.idcheck.iproov.testdouble.usecases.LaunchFaceScanFakeImpl

object IproovTestDoubleHiltConfig {
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object FakeFaceScannerActivityRetainedModule {
        @ActivityRetainedScoped
        @Provides
        fun providesFaceScanner(scanner: FaceScannerFakeImpl): FaceScanner = scanner
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object LaunchFaceScanFakeViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesLaunchFaceScan(launcher: LaunchFaceScanFakeImpl): LaunchFaceScan = launcher
    }
}
