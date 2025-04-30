package uk.gov.idcheck.readid.common.impl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.idcheck.readid.common.api.brp.mrz.BrpMrzValidator
import uk.gov.idcheck.readid.common.api.brp.nfc.BrpNfcScanner
import uk.gov.idcheck.readid.common.api.brp.photo.BrpPhotoScanner
import uk.gov.idcheck.readid.common.api.dispatchers.IReadIdForegroundDispatcher
import uk.gov.idcheck.readid.common.api.driving.DrivingScanner
import uk.gov.idcheck.readid.common.api.passport.nfc.PassportNfcScanner
import uk.gov.idcheck.readid.common.api.passport.photo.PassportPhotoScanner
import uk.gov.idcheck.readid.common.impl.brp.mrz.BrpMrzValidatorImpl
import uk.gov.idcheck.readid.common.impl.brp.nfc.BrpNfcScannerImpl
import uk.gov.idcheck.readid.common.impl.brp.photo.BrpPhotoScannerImpl
import uk.gov.idcheck.readid.common.impl.dispatchers.ReadIdForegroundDispatcher
import uk.gov.idcheck.readid.common.impl.driving.DrivingScannerImpl
import uk.gov.idcheck.readid.common.impl.passport.nfc.PassportNfcScannerImpl
import uk.gov.idcheck.readid.common.impl.passport.photo.PassportPhotoScannerImpl

object ReadidCommonImplHiltConfig {
    @InstallIn(ViewModelComponent::class)
    @Module
    object BrpMrzValidatorModule {
        @Provides
        @ViewModelScoped
        fun providesBrpMrzValidator(scanner: BrpMrzValidatorImpl): BrpMrzValidator = scanner
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object BrpNfcScannerModule {
        @Provides
        @ViewModelScoped
        fun providesBrpNfcScanner(scanner: BrpNfcScannerImpl): BrpNfcScanner = scanner
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object BrpPhotoScannerModule {
        @Provides
        @ViewModelScoped
        fun providesBrpPhotoScanner(scanner: BrpPhotoScannerImpl): BrpPhotoScanner = scanner
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object ReadIDUtilModule {
        @Provides
        @Singleton
        fun providesReadIdUtil(
            dispatcher: ReadIdForegroundDispatcher
        ): IReadIdForegroundDispatcher = dispatcher
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object DrivingScannerModule {
        @Provides
        @ViewModelScoped
        fun providesDrivingScanner(scanner: DrivingScannerImpl): DrivingScanner = scanner
    }

    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object PassportNfcScannerModule {
        @ActivityRetainedScoped
        @Provides
        fun providesPassportImageScanner(scanner: PassportNfcScannerImpl): PassportNfcScanner =
            scanner
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object PassportPhotoScannerModule {
        @Provides
        @ViewModelScoped
        fun providesPassportImageScanner(scanner: PassportPhotoScannerImpl): PassportPhotoScanner =
            scanner
    }
}
