package uk.gov.idcheck.repositories.impl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import uk.gov.idcheck.repositories.api.chipDocument.image.ChippedDocImageSession
import uk.gov.idcheck.repositories.api.chipDocument.nfc.ChippedDocNfcSession
import uk.gov.idcheck.repositories.api.driving.DrivingSession
import uk.gov.idcheck.repositories.api.driving.scan.back.GetBackScan
import uk.gov.idcheck.repositories.api.driving.scan.back.SetBackScan
import uk.gov.idcheck.repositories.api.driving.scan.front.GetFrontScan
import uk.gov.idcheck.repositories.api.driving.scan.front.SetFrontScan
import uk.gov.idcheck.repositories.api.face.FaceSession
import uk.gov.idcheck.repositories.api.face.attemptsremaining.ResetFaceScanRepository
import uk.gov.idcheck.repositories.api.face.attemptsremaining.SetFaceScanAttemptsRemaining
import uk.gov.idcheck.repositories.api.offboarding.OffBoardingSession
import uk.gov.idcheck.repositories.api.offboarding.attemptsremaining.GetConfirmIdentityAttemptsRemaining
import uk.gov.idcheck.repositories.api.offboarding.attemptsremaining.SetConfirmIdentityAttemptsRemaining
import uk.gov.idcheck.repositories.api.usecases.RepositoryResetter
import uk.gov.idcheck.repositories.api.usecases.ResetApp
import uk.gov.idcheck.repositories.api.vendor.ReadIdSession
import uk.gov.idcheck.repositories.api.vendor.VendorSession
import uk.gov.idcheck.repositories.api.vendor.session.ResetReadIdSession
import uk.gov.idcheck.repositories.api.webhandover.WebHandoverSession
import uk.gov.idcheck.repositories.api.webhandover.documenttype.GetDocumentType
import uk.gov.idcheck.repositories.api.webhandover.documenttype.SetDocumentType
import uk.gov.idcheck.repositories.api.webhandover.journeytype.GetUserJourneyType
import uk.gov.idcheck.repositories.api.webhandover.sessionid.GetSessionId
import uk.gov.idcheck.repositories.api.webhandover.sessionid.SessionIdValidator
import uk.gov.idcheck.repositories.api.webhandover.sessionid.SetSessionId
import uk.gov.idcheck.repositories.impl.driving.DrivingSessionRepository
import uk.gov.idcheck.repositories.impl.face.FaceSessionRepository
import uk.gov.idcheck.repositories.impl.offboarding.OffBoardingSessionRepository
import uk.gov.idcheck.repositories.impl.usecases.RepositoriesToReset
import uk.gov.idcheck.repositories.impl.usecases.ResetAppImpl
import uk.gov.idcheck.repositories.impl.vendor.VendorSessionRepository
import uk.gov.idcheck.repositories.impl.webhandover.WebHandoverRepository
import uk.gov.idcheck.repositories.impl.webhandover.sessionid.ValidSessionIdImpl

object RepositoriesImplHiltConfig {
    @InstallIn(ViewModelComponent::class)
    @Module
    object SessionIdUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesGetSessionId(reader: WebHandoverSession.Reader): GetSessionId =
            GetSessionId {
                reader.getSessionId()
            }

        @Provides
        @ViewModelScoped
        fun providesSetSessionId(writer: WebHandoverSession.Writer): SetSessionId =
            SetSessionId { sessionId ->
                writer.updateSessionId(sessionId)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object SessionIdValidatorUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesValidSessionId(validSessionId: ValidSessionIdImpl): SessionIdValidator =
            validSessionId
    }

    object WebHandoverSessionModule {
        /**
         * Hilt injection module for [WebHandoverSession] objects bound to the activity scope.
         *
         * The provided bindings have the same lifecycle as a configuration surviving activity.
         */
        @InstallIn(ActivityRetainedComponent::class)
        @Module
        object WebHandoverSessionActivityModule {
            /**
             * Creates an [Inject] binding for the [WebHandoverSession.Repository] class.
             *
             * This means that the interface is accessible within Activities, Fragments and ViewModels.
             *
             * @param repository The concrete class with a [Inject] constructor to be
             * used as the underlying implementation for the [WebHandoverSession.Repository] interface.
             */
            @ActivityRetainedScoped
            @Provides
            fun providesWebHandoverSessionRepository(
                repository: WebHandoverRepository
            ): WebHandoverSession.Repository = repository

            /**
             * Creates an [Inject] binding for the [WebHandoverSession.Reader] interface.
             *
             * This means that the interface is accessible within Activities, Fragments and ViewModels.
             *
             * @param repository A previously bound [WebHandoverSession.Repository] that's used as the
             * underlying implementation, narrowing the scope of the provided object.
             */
            @ActivityRetainedScoped
            @Provides
            fun providesWebHandoverSessionReader(
                repository: WebHandoverSession.Repository
            ): WebHandoverSession.Reader = repository
        }

        /**
         * Hilt injection module for [WebHandoverSession] objects bound to the ViewModel scope.
         *
         * The provided bindings aren't available within Activities or Fragments; only ViewModels.
         */
        @InstallIn(ViewModelComponent::class)
        @Module
        object WebHandoverSessionViewModelModule {
            /**
             * Creates an [Inject] binding for the [WebHandoverSession.Writer] interface.
             *
             * This means that an object matching this interface is only accessible within ViewModels.
             * Therefore, there would be no need to use a concrete class that matches the
             * [WebHandoverSession.Writer] interface.
             *
             * @param repository A previously bound [WebHandoverSession.Repository] that's used as the
             * underlying implementation, narrowing the scope of the provided object.
             */
            @Provides
            @ViewModelScoped
            fun providesWebHandoverSessionWriter(
                repository: WebHandoverSession.Repository
            ): WebHandoverSession.Writer = repository
        }
    }

    /**
     * Hilt injection module for [DrivingSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object DrivingSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [DrivingSession.Repository] class.
         *
         * This means that the interface are accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor to be
         * used as the underlying implementation for the [DrivingSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesDrivingSessionRepository(
            repository: DrivingSessionRepository
        ): DrivingSession.Repository = repository
    }

    /**
     * Hilt injection module for [DrivingSession] objects bound to the ViewModel scope.
     *
     * The provided bindings aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object DrivingSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [DrivingSession.Reader] interface.
         *
         * This means that an object matching this interface are only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [DrivingSession.Reader] interface.
         *
         * @param repository A previously bound [DrivingSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesDrivingSessionReader(
            repository: DrivingSession.Repository
        ): DrivingSession.Reader = repository

        /**
         * Creates an [Inject] binding for the [DrivingSession.Writer] interface.
         *
         * This means that an object matching this interface are only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [DrivingSession.Writer] interface.
         *
         * @param repository A previously bound [DrivingSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesDrivingSessionWriter(
            repository: DrivingSession.Repository
        ): DrivingSession.Writer = repository
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object BackScanUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesGetBackScan(reader: DrivingSession.Reader): GetBackScan =
            GetBackScan {
                reader.getBackScan()
            }

        @Provides
        @ViewModelScoped
        fun providesSetBackScan(writer: DrivingSession.Writer): SetBackScan =
            SetBackScan { backScan ->
                writer.updateBackScan(backScan)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object FrontScanViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesGetFrontScan(reader: DrivingSession.Reader): GetFrontScan =
            GetFrontScan {
                reader.getFrontScan()
            }

        @Provides
        @ViewModelScoped
        fun providesSetFrontScan(writer: DrivingSession.Writer): SetFrontScan =
            SetFrontScan { frontScan ->
                writer.updateFrontScan(frontScan)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object FaceScanUseCasesModule {
        @Provides
        @ViewModelScoped
        fun providesSetFaceScanAttemptsRemaining(
            writer: FaceSession.Writer
        ): SetFaceScanAttemptsRemaining =
            SetFaceScanAttemptsRemaining { attemptsLeft ->
                writer.updateFaceScanAttemptsRemaining(attemptsLeft)
            }

        @Provides
        @ViewModelScoped
        fun providesResetFaceScanRepository(
            repository: FaceSession.Repository
        ): ResetFaceScanRepository =
            ResetFaceScanRepository {
                repository.resetRepository()
            }
    }

    /**
     * Hilt injection module for [FaceSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object FaceSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [FaceSession.Repository] class.
         *
         * This means that the interface are accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor to be
         * used as the underlying implementation for the [FaceSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesFaceSessionRepository(
            repository: FaceSessionRepository
        ): FaceSession.Repository = repository
    }

    /**
     * Hilt injection module for [FaceSession] objects bound to the ViewModel scope.
     *
     * The provided bindings aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object FaceSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [FaceSession.Reader] interface.
         *
         * This means that an object matching this interface are only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [FaceSession.Reader] interface.
         *
         * @param repository A previously bound [FaceSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesFaceSessionReader(repository: FaceSession.Repository): FaceSession.Reader =
            repository

        /**
         * Creates an [Inject] binding for the [FaceSession.Writer] interface.
         *
         * This means that an object matching this interface are only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [FaceSession.Writer] interface.
         *
         * @param repository A previously bound [FaceSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesFaceSessionWriter(repository: FaceSession.Repository): FaceSession.Writer =
            repository
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object ConfirmIdentityAttemptsModule {
        @Provides
        @ViewModelScoped
        fun providesGetConfirmIdentityAttemptsRemaining(
            reader: OffBoardingSession.Reader
        ): GetConfirmIdentityAttemptsRemaining =
            GetConfirmIdentityAttemptsRemaining {
                reader.getConfirmIdentityAttemptsRemaining()
            }

        @Provides
        @ViewModelScoped
        fun providesSetConfirmIdentityAttemptsRemaining(
            writer: OffBoardingSession.Writer
        ): SetConfirmIdentityAttemptsRemaining =
            SetConfirmIdentityAttemptsRemaining { attemptsLeft ->
                writer.setConfirmIdentityAttemptsRemaining(attemptsLeft)
            }
    }

    /**
     * Hilt injection module for [OffBoardingSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object OffBoardingSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [OffBoardingSession.Repository] class.
         *
         * This means that the interface is accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor to be
         * used as the underlying implementation for the [OffBoardingSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesOffBoardingSessionRepository(
            repository: OffBoardingSessionRepository
        ): OffBoardingSession.Repository = repository
    }

    /**
     * Hilt injection module for [OffBoardingSession] objects bound to the ViewModel scope.
     *
     * The provided bindings aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object OffBoardingSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [OffBoardingSession.Reader] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [OffBoardingSession.Reader] interface.
         *
         * @param repository A previously bound [OffBoardingSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesOffBoardingSessionReader(
            repository: OffBoardingSession.Repository
        ): OffBoardingSession.Reader = repository

        /**
         * Creates an [Inject] binding for the [OffBoardingSession.Writer] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [OffBoardingSession.Writer] interface.
         *
         * @param repository A previously bound [OffBoardingSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesOffBoardingSessionWriter(
            repository: OffBoardingSession.Repository
        ): OffBoardingSession.Writer = repository
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object RepositoryResetterListModule {
        @Provides
        @RepositoriesToReset
        @Suppress("LongParameterList")
        @ViewModelScoped
        fun providesRepositoriesListForResetting(
            chippedDocImageSession: ChippedDocImageSession.Repository,
            chippedDocNfcSession: ChippedDocNfcSession.Repository,
            drivingScansRepository: DrivingSession.Repository,
            faceSessionRepository: FaceSession.Repository,
            offBoardingSessionRepository: OffBoardingSession.Repository,
            vendorSessionRepository: VendorSession.Repository,
            webHandoverSessionRepository: WebHandoverSession.Repository
        ): Iterable<RepositoryResetter> =
            listOf(
                chippedDocImageSession,
                chippedDocNfcSession,
                drivingScansRepository,
                faceSessionRepository,
                offBoardingSessionRepository,
                vendorSessionRepository,
                webHandoverSessionRepository
            )
    }

    /**
     * Hilt injection module for the [ResetApp] interface
     *
     * The provided bindings have the same lifecycle as ViewModels, meaning that the provided
     * objects are unavailable within Activities and Fragments.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object RepositoryResetterModule {
        @Provides
        @ViewModelScoped
        fun providesResetApp(resetApp: ResetAppImpl): ResetApp = resetApp
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object ResetReadIdSessionUseCaseModule {
        @Provides
        @ViewModelScoped
        fun providesResetReadIdSession(writer: ReadIdSession.Writer): ResetReadIdSession =
            ResetReadIdSession {
                writer.updateReadIdSession(null)
            }
    }

    /**
     * Hilt injection module for [ReadIdSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object ReadIdSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [ReadIdSession.Repository] class.
         *
         * This means that the interface can is accessible within Activities, Fragments
         * and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor to be
         * used as the underlying implementation for the [ReadIdSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesReadIdSessionRepository(
            repository: VendorSessionRepository
        ): ReadIdSession.Repository = repository

        /**
         * Creates an [Inject] binding for the [ReadIdSession.Reader] interface.
         *
         * This means that the interface can is accessible within Activities, Fragments and ViewModels.
         *
         * @param repository A previously bound [ReadIdSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesReadIdSessionReader(
            repository: ReadIdSession.Repository
        ): ReadIdSession.Reader = repository
    }

    /**
     * Hilt injection module for [ReadIdSession] objects bound to the ViewModel scope.
     *
     * The bindings provided aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object ReadIdSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [ReadIdSession.Writer] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [ReadIdSession.Writer] interface.
         *
         * @param repository A previously bound [ReadIdSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesReadIdSessionWriter(
            repository: ReadIdSession.Repository
        ): ReadIdSession.Writer = repository
    }

    /**
     * Hilt injection module for [VendorSession] objects bound to the activity scope.
     *
     * The provided bindings have the same lifecycle as a configuration surviving activity.
     */
    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object VendorSessionActivityModule {
        /**
         * Creates an [Inject] binding for the [VendorSession.Repository] class.
         *
         * This means that the interface can is accessible within Activities, Fragments and ViewModels.
         *
         * @param repository The concrete class with a [Inject] constructor to be
         * used as the underlying implementation for the [VendorSession.Repository] interface.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesVendorSessionRepository(
            repository: VendorSessionRepository
        ): VendorSession.Repository = repository

        /**
         * Creates an [Inject] binding for the [VendorSession.Reader] interface.
         *
         * This means that the interface can is accessible within Activities, Fragments and ViewModels.
         *
         * @param repository A previously bound [VendorSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @ActivityRetainedScoped
        @Provides
        fun providesVendorSessionReader(
            repository: VendorSession.Repository
        ): VendorSession.Reader = repository
    }

    /**
     * Hilt injection module for [VendorSession] objects bound to the ViewModel scope.
     *
     * The bindings provided aren't available within Activities or Fragments; only ViewModels.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object VendorSessionViewModelModule {
        /**
         * Creates an [Inject] binding for the [VendorSession.Writer] interface.
         *
         * This means that an object matching this interface is only accessible within ViewModels.
         * Therefore, there would be no need to use a concrete class that matches the
         * [VendorSession.Writer] interface.
         *
         * @param repository A previously bound [VendorSession.Repository] that's used as the
         * underlying implementation, narrowing the scope of the provided object.
         */
        @Provides
        @ViewModelScoped
        fun providesVendorSessionWriter(
            repository: VendorSession.Repository
        ): VendorSession.Writer = repository
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object DocumentTypeUseCasesModule {
        @Provides
        @ViewModelScoped
        fun providesDocumentTypeReader(reader: WebHandoverSession.Reader): GetDocumentType =
            GetDocumentType {
                reader.getDocumentType()
            }

        @Provides
        @ViewModelScoped
        fun providesDocumentTypeWriter(writer: WebHandoverSession.Writer): SetDocumentType =
            SetDocumentType { type ->
                writer.updateDocumentType(type)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object UserJourneyTypeUseCasesModule {
        @Provides
        @ViewModelScoped
        fun providesGetUserJourneyType(reader: WebHandoverSession.Reader): GetUserJourneyType =
            GetUserJourneyType {
                reader.getJourneyType()
            }
    }
}
