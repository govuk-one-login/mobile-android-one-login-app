package uk.gov.idcheck.sdk

import android.content.Context
import android.nfc.NfcManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import uk.gov.idcheck.iproov.api.FaceScanner
import uk.gov.idcheck.repositories.api.offboarding.OffBoardingSession
import uk.gov.idcheck.sdk.face.veriff.VeriffInteractor
import uk.gov.idcheck.sdk.face.veriff.VeriffInteractorImpl
import uk.gov.idcheck.sdk.offboarding.usecases.GetVeriffStatus
import uk.gov.idcheck.sdk.offboarding.usecases.SetVeriffStatus
import uk.gov.idcheck.sdk.offboarding.usecases.biosession.FinishBioSession
import uk.gov.idcheck.sdk.offboarding.usecases.biosession.FinishBioSessionImpl
import uk.gov.idcheck.sdk.passport.nfc.checker.NfcChecker
import uk.gov.idcheck.sdk.session.IdCheckSdkSession
import uk.gov.idcheck.sdk.session.IdCheckSdkSessionRepository
import uk.gov.idcheck.sdk.usecases.ValidateVerifyToken
import uk.gov.idcheck.sdk.usecases.veriff.StartVeriff
import uk.gov.idcheck.sdk.usecases.veriff.StartVeriffImpl
import javax.inject.Singleton

object IdCheckSdkHiltConfig {
    @Module
    @InstallIn(ViewModelComponent::class)
    object FinishBioSessionViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesFinishBioSession(finishBioSession: FinishBioSessionImpl): FinishBioSession =
            finishBioSession
    }

    /**
     * Contains Dependency Injection (DI) configurations relating to the [IdCheckSdkSession].
     *
     * See:
     * - [Overview of hilt components](https://dagger.dev/hilt/components.html) to learn about
     *   component bindings, lifetime cycles and scopes.
     */
    object IdCheckSdkSessionHiltModule {
        /**
         * Contains [ActivityRetainedScoped] DI configurations.
         *
         * These can be [Injected][javax.inject.Inject] within Activity, ViewModel and Fragment classes.
         */
        @InstallIn(ActivityRetainedComponent::class)
        @Module
        object ActivityRetainedModule {
            /**
             * Declares the [Injected][javax.inject.Inject] [repository] parameter for use when
             * requesting for an Injected [IdCheckSdkSession.Repository].
             *
             * @see ViewModelModule.providesIdCheckSessionReader
             * @see ViewModelModule.providesIdCheckSessionWriter
             */
            @Provides
            @ActivityRetainedScoped
            fun providesIdCheckSessionRepository(
                repository: IdCheckSdkSessionRepository,
            ): IdCheckSdkSession.Repository = repository
        }

        /**
         * Contains [ViewModelScoped] DI configurations.
         *
         * These can be [Injected][javax.inject.Inject] within only ViewModel classes.
         */
        @InstallIn(ViewModelComponent::class)
        @Module
        object ViewModelModule {
            /**
             * Declares the [Injected][javax.inject.Inject] [repository] parameter for use when
             * requesting for an Injected [IdCheckSdkSession.Reader].
             *
             * @see ActivityRetainedModule.providesIdCheckSessionRepository
             */
            @Provides
            @ViewModelScoped
            fun providesIdCheckSessionReader(
                repository: IdCheckSdkSession.Repository,
            ): IdCheckSdkSession.Reader = repository

            /**
             * Declares the [Injected][javax.inject.Inject] [repository] parameter for use when
             * requesting for an Injected [IdCheckSdkSession.Writer].
             *
             * @see ActivityRetainedModule.providesIdCheckSessionRepository
             */
            @Provides
            @ViewModelScoped
            fun providesIdCheckSessionWriter(
                repository: IdCheckSdkSession.Repository,
            ): IdCheckSdkSession.Writer = repository
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object NfcCheckerModule {
        @Provides
        @Singleton
        fun providesNfcChecker(manager: NfcManager): NfcChecker =
            NfcChecker {
                manager.defaultAdapter != null
            }
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object NfcServiceSingletonModule {
        @Provides
        @Singleton
        fun providesNfcManager(
            @ApplicationContext
            context: Context,
        ): NfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object OffboardingUseCaseViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesGetVeriffStatus(reader: OffBoardingSession.Reader): GetVeriffStatus =
            GetVeriffStatus {
                reader.getVeriffStatus()
            }

        @Provides
        @ViewModelScoped
        fun providesSetVeriffStatus(writer: OffBoardingSession.Writer): SetVeriffStatus =
            SetVeriffStatus { status ->
                writer.updateVeriffStatus(status)
            }
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object StartVeriffViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesStartVeriff(startVeriff: StartVeriffImpl): StartVeriff = startVeriff
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object ValidateVerifyTokenViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesValidateVerifyToken(scanner: FaceScanner): ValidateVerifyToken =
            ValidateVerifyToken { callback ->
                scanner.validateVerifyToken(callback)
            }
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    object VeriffInteractorViewModelModule {
        @Provides
        @ViewModelScoped
        fun providesVeriffInteractor(interactor: VeriffInteractorImpl): VeriffInteractor =
            interactor
    }
}
