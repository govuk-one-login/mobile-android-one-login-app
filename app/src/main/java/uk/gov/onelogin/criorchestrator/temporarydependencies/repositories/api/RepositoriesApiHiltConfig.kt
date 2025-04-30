package uk.gov.idcheck.repositories.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import uk.gov.idcheck.repositories.api.chipDocument.attemptsremaining.GetChipCloneAttemptsRemaining
import uk.gov.idcheck.repositories.api.chipDocument.attemptsremaining.SetChipCloneAttemptsRemaining
import uk.gov.idcheck.repositories.api.chipDocument.image.ChippedDocImageSession
import uk.gov.idcheck.repositories.api.chipDocument.image.DocumentImageSessionRepository
import uk.gov.idcheck.repositories.api.chipDocument.image.scan.GetDocImageScan
import uk.gov.idcheck.repositories.api.chipDocument.image.scan.SetDocImageScan
import uk.gov.idcheck.repositories.api.chipDocument.nfc.ChippedDocNfcSession
import uk.gov.idcheck.repositories.api.chipDocument.nfc.DocumentNfcSessionRepository
import uk.gov.idcheck.repositories.api.chipDocument.nfc.scan.GetDocNfcScan
import uk.gov.idcheck.repositories.api.chipDocument.nfc.scan.SetDocNfcScan
import uk.gov.idcheck.repositories.api.vendor.VendorSession
import uk.gov.idcheck.repositories.api.vendor.token.GetIproovToken
import uk.gov.idcheck.repositories.api.vendor.token.ResetIproovToken
import uk.gov.idcheck.repositories.api.vendor.token.SetIproovToken

object RepositoriesApiHiltConfig {
    @InstallIn(ViewModelComponent::class)
    @Module
    object ChipCloneAttemptsRemainingUseCaseModule {
        @ViewModelScoped
        @Provides
        fun providesGetChipCloneAttemptsRemaining(
            reader: ChippedDocNfcSession.Reader
        ): GetChipCloneAttemptsRemaining =
            GetChipCloneAttemptsRemaining {
                reader.getChipCloneAttemptRemaining()
            }

        @ViewModelScoped // Lowest hilt injection level
        @Provides
        fun providesSetChipCloneAttemptsRemaining(
            writer: ChippedDocNfcSession.Writer
        ): SetChipCloneAttemptsRemaining =
            SetChipCloneAttemptsRemaining { attemptsRemaining ->
                writer.updateChipCloneAttemptRemaining(attemptsRemaining)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object ChippedDocumentImageScanViewModelModule {
        @ViewModelScoped
        @Provides
        fun providesGetPassportImageScan(reader: ChippedDocImageSession.Reader): GetDocImageScan =
            GetDocImageScan {
                reader.getDocImageScan()
            }

        @ViewModelScoped
        @Provides
        fun providesSetPassportImageScan(writer: ChippedDocImageSession.Writer): SetDocImageScan =
            SetDocImageScan { imageScan ->
                writer.updateDocImageScan(imageScan)
            }
    }

    /**
     * Hilt injection module for [ChippedDocImageSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object ChipDocSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.Repository] class.
         *
         * This means that the interface are accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor, used as the
         * underlying implementation for the [ChippedDocImageSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesChipDocSessionPhotoRepository(
            repository: DocumentImageSessionRepository
        ): ChippedDocImageSession.Repository = repository

        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.NfcRepository] class.
         *
         * This means that the interface are accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor, used as the underlying
         * implementation for the [ChippedDocImageSession.NfcRepository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesChipDocSessionNfcRepository(
            repository: DocumentNfcSessionRepository
        ): ChippedDocNfcSession.Repository = repository

        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.NfcReader] interface.
         *
         * This means that the interface is accessible within Activities, Fragments and
         * ViewModels.
         *
         * @param repository A previously bound [ChippedDocImageSession.NfcRepository] that's used
         * as the underlying implementation, narrowing the scope of the provided object.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesChipDocSessionNfcReader(
            repository: ChippedDocNfcSession.Repository
        ): ChippedDocNfcSession.Reader = repository

        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.NfcWriter] interface.
         *
         * This means that the interface is accessible within Activities, Fragments and
         * ViewModels.
         *
         * @param repository A previously bound [ChippedDocImageSession.NfcRepository] that's used
         * as the underlying implementation, narrowing the scope of the provided object.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesChipDocSessionNfcWriter(
            repository: ChippedDocNfcSession.Repository
        ): ChippedDocNfcSession.Writer = repository
    }

    /**
     * Hilt injection module for [ChippedDocImageSession] objects bound to the ViewModel scope.
     *
     * The provided bindings aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object ChipDocSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.Reader] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [ChippedDocImageSession.Reader] interface.
         *
         * @param repository A previously bound [ChippedDocImageSession.Repository] that's used as
         * the underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesChipDocSessionPhotoReader(
            repository: ChippedDocImageSession.Repository
        ): ChippedDocImageSession.Reader = repository

        /**
         * Creates an [Inject] binding for the [ChippedDocImageSession.Writer] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [ChippedDocImageSession.Writer] interface.
         *
         * @param repository A previously bound [ChippedDocImageSession.Repository] that's used as
         * the underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesChipDocSessionPhotoWriter(
            repository: ChippedDocImageSession.Repository
        ): ChippedDocImageSession.Writer = repository
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object PassportNfcScanUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesGetNfcScan(reader: ChippedDocNfcSession.Reader): GetDocNfcScan =
            GetDocNfcScan {
                reader.getNfcChipScan()
            }

        @Provides
        @ViewModelScoped
        fun providesSetPassportNfcScan(writer: ChippedDocNfcSession.Writer): SetDocNfcScan =
            SetDocNfcScan { nfcChipScan ->
                writer.updateNfcChipScan(nfcChipScan)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object IproovTokenUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesGetIproovToken(reader: VendorSession.Reader): GetIproovToken =
            GetIproovToken {
                reader.getIproovToken()
            }

        @Provides
        @ViewModelScoped
        fun providesResetIproovToken(writer: VendorSession.Writer): ResetIproovToken =
            ResetIproovToken {
                writer.updateIproovToken(null)
            }

        @Provides
        @ViewModelScoped
        fun providesSetIproovToken(writer: VendorSession.Writer): SetIproovToken =
            SetIproovToken { token ->
                writer.updateIproovToken(token)
            }
    }
}
