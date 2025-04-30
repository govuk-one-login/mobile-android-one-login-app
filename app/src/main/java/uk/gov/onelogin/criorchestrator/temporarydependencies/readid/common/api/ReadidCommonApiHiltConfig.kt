package uk.gov.idcheck.readid.common.api

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import uk.gov.idcheck.features.api.FeatureFlags
import uk.gov.idcheck.readid.common.api.dispatchers.usecases.DisableForegroundDispatcherUsecase
import uk.gov.idcheck.readid.common.api.dispatchers.usecases.EnableForegroundDispatcherUsecase
import uk.gov.idcheck.readid.common.api.dispatchers.usecases.IDisableForegroundDispatcherUsecase
import uk.gov.idcheck.readid.common.api.dispatchers.usecases.IEnableForegroundDispatcherUsecase
import uk.gov.idcheck.readid.common.api.driving.usecases.CommitReadId
import uk.gov.idcheck.readid.common.api.driving.usecases.CommitReadIdImpl
import uk.gov.idcheck.readid.common.api.driving.usecases.GetDrivingScannerResults
import uk.gov.idcheck.readid.common.api.driving.usecases.GetDrivingScannerResultsImpl
import uk.gov.idcheck.readid.common.api.driving.usecases.StartDrivingScan
import uk.gov.idcheck.readid.common.api.driving.usecases.StartDrivingScanImpl
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.CommitNfcReadId
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.CommitNfcReadIdImpl
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.GetPassportNfcScannerResults
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.GetPassportNfcScannerResultsImpl
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.StartPassportNfcScan
import uk.gov.idcheck.readid.common.api.passport.nfc.usecases.StartPassportNfcScanImpl
import uk.gov.idcheck.readid.common.api.passport.photo.events.IPassportPhotoScannerEventLogger
import uk.gov.idcheck.readid.common.api.passport.photo.events.PassportPhotoScanFeatures.ENHANCED_PASSPORT_PHOTO_LOGGING
import uk.gov.idcheck.readid.common.api.passport.photo.events.PassportPhotoScannerEventLogger
import uk.gov.idcheck.readid.common.api.passport.photo.events.PassportPhotoScannerNoOpLogger
import uk.gov.idcheck.readid.common.api.passport.photo.usecases.GetPassportImageScannerResults
import uk.gov.idcheck.readid.common.api.passport.photo.usecases.GetPassportImageScannerResultsImpl
import uk.gov.idcheck.readid.common.api.passport.photo.usecases.StartPassportImageScan
import uk.gov.idcheck.readid.common.api.passport.photo.usecases.StartPassportImageScanImpl

object ReadidCommonApiHiltConfig {
    @Module
    @InstallIn(ViewModelComponent::class)
    object PassportScannerResultFetcherModule {
        @Provides
        @ViewModelScoped
        fun providesGetPassportImageScannerResult(
            getPassportImageScannerResults: GetPassportImageScannerResultsImpl
        ): GetPassportImageScannerResults = getPassportImageScannerResults
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object StartPassportUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesStartPassportImageScan(
            useCases: StartPassportImageScanImpl
        ): StartPassportImageScan = useCases
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object DrivingScannerResultModule {
        /**
         * Creates an [Inject] binding for the [GetDrivingScannerResults] interface.
         *
         * Scoped at the ViewModel level, it's inaccessible within Activities and Fragments.
         */
        @Provides
        @ViewModelScoped
        fun providesGetScannerResult(
            scannerResultFetcher: GetDrivingScannerResultsImpl
        ): GetDrivingScannerResults = scannerResultFetcher
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object DrivingUseCaseViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesCommitReadId(commitReadId: CommitReadIdImpl): CommitReadId = commitReadId

        @Provides
        @ViewModelScoped
        fun providesStartDrivingScan(startDrivingScan: StartDrivingScanImpl): StartDrivingScan =
            startDrivingScan
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object UsecaseModule {
        @Provides
        @ActivityScoped
        fun providesDisableForegroundDispatcherUsecase(
            disableForegroundDispatcherUsecase: DisableForegroundDispatcherUsecase
        ): IDisableForegroundDispatcherUsecase = disableForegroundDispatcherUsecase

        @Provides
        @ActivityScoped
        fun providesEnableForegroundDispatcherUsecase(
            enableForegroundDispatcherUsecase: EnableForegroundDispatcherUsecase
        ): IEnableForegroundDispatcherUsecase = enableForegroundDispatcherUsecase
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object CommitNfcReadIdModule {
        @Provides
        @ViewModelScoped
        fun providesCommitNfcReadID(useCase: CommitNfcReadIdImpl): CommitNfcReadId = useCase
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object GetPassportNfcScannerResultsModule {
        @Provides
        @ViewModelScoped
        fun providesGetPassportNfcScannerResults(
            getPassportNfcScan: GetPassportNfcScannerResultsImpl
        ): GetPassportNfcScannerResults = getPassportNfcScan
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object StartPassportNfcScanModule {
        @Provides
        @ViewModelScoped
        fun providesStartPassportNfcScan(
            startPassportNfcScan: StartPassportNfcScanImpl
        ): StartPassportNfcScan = startPassportNfcScan
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object PassportPhotoScannerEventsModule {
        @Provides
        @ViewModelScoped
        fun providesPassportPhotoScannerEventsLogger(
            featureFlags: FeatureFlags,
            eventLogger: PassportPhotoScannerEventLogger,
            noOpLogger: PassportPhotoScannerNoOpLogger
        ): IPassportPhotoScannerEventLogger =
            if (
                featureFlags[ENHANCED_PASSPORT_PHOTO_LOGGING]
            ) {
                eventLogger
            } else {
                noOpLogger
            }

        @Provides
        @ViewModelScoped
        fun providesGsonBuilder(): GsonBuilder = GsonBuilder().setPrettyPrinting()
    }
}
