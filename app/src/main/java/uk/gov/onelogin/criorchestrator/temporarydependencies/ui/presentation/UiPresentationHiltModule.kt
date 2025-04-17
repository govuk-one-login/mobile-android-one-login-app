package uk.gov.idcheck.ui.presentation

import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.Builder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import uk.gov.idcheck.features.api.FeatureFlags
import uk.gov.idcheck.features.api.permissions.PermissionStateHandler.Granted
import uk.gov.idcheck.sdk.utils.buttons.support.SupportFormUriV1
import uk.gov.idcheck.sdk.utils.buttons.support.SupportFormUriV2
import uk.gov.idcheck.sdk.utils.links.generic.SupportLinkUriImpl
import uk.gov.idcheck.ui.presentation.buttons.support.SupportButtonFeatureFlag.SUPPORT_BUTTON
import uk.gov.idcheck.ui.presentation.buttons.support.SupportFormUri
import uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway.ConfirmIdentityAnotherWayBuilder
import uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway.ConfirmIdentityAnotherWayBuilderImpl
import uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway.ConfirmIdentityAnotherWayJourneyController
import uk.gov.idcheck.ui.presentation.dialogs.confirmanotherway.ConfirmIdentityAnotherWayJourneyControllerImpl
import uk.gov.idcheck.ui.presentation.links.generic.GenericLinkUri
import uk.gov.idcheck.ui.presentation.navigation.DirectionsLauncher
import uk.gov.idcheck.ui.presentation.navigation.FragmentActivityDirectionsLauncher
import uk.gov.idcheck.ui.presentation.permission.camera.errors.CameraPermissionErrorCallback
import uk.gov.idcheck.ui.presentation.permission.camera.errors.CameraPermissionErrorGrantedCallback
import uk.gov.idcheck.ui.presentation.snackbar.SnackbarFactory
import uk.gov.idcheck.ui.presentation.snackbar.SnackbarFactoryImpl
import uk.gov.idcheck.ui.presentation.usecases.abort.AbortJourneyApiCall
import uk.gov.idcheck.ui.presentation.usecases.abort.AbortJourneyApiCallImpl
import uk.gov.idcheck.ui.presentation.usecases.handback.Handback
import uk.gov.idcheck.ui.presentation.usecases.handback.HandbackImpl
import uk.gov.idcheck.ui.presentation.web.customtabs.CustomTabsIntentLauncher
import uk.gov.idcheck.ui.presentation.web.customtabs.CustomTabsLauncher
import javax.inject.Singleton

object UiPresentationHiltModule {
    @InstallIn(SingletonComponent::class)
    @Module
    object CustomTabsIntentModule {
        @Provides
        @Singleton
        fun providesCustomTabsIntentBuilder(): Builder =
            Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object CustomTabsLauncherModule {
        /**
         * Injects a single instance across the entire app. This is due to custom constraint layouts.
         */
        @Provides
        @Singleton
        fun providesCustomTabsLauncher(
            intentLauncher: CustomTabsIntentLauncher,
        ): CustomTabsLauncher = intentLauncher
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object SnackbarFactoryModule {
        @Provides
        @ActivityScoped
        fun providesSnackbarFactory(factoryImpl: SnackbarFactoryImpl): SnackbarFactory = factoryImpl
    }

    @InstallIn(FragmentComponent::class)
    @Module
    object CameraPermissionErrorGrantedCallbackModule {
        @CameraPermissionErrorCallback
        @FragmentScoped
        @Provides
        fun providesCameraPermissionErrorGrantedCallback(
            callback: CameraPermissionErrorGrantedCallback,
        ): Granted = callback
    }

    @InstallIn(ActivityComponent::class)
    @Module
    object DirectionsLauncherFragmentModule {
        @Provides
        @ActivityScoped
        fun providesFragmentDirectionsLauncher(
            launcher: FragmentActivityDirectionsLauncher,
        ): DirectionsLauncher = launcher
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object GenericLinkUriModule {
        @Provides
        @ActivityScoped
        fun providesSupportLinkUri(supportLinkUri: SupportLinkUriImpl): GenericLinkUri =
            supportLinkUri
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object ConfirmIdentityAnotherWayBuilderModule {
        @Provides
        @ActivityScoped
        fun providesConfirmAnotherWayBuilder(): ConfirmIdentityAnotherWayBuilder =
            ConfirmIdentityAnotherWayBuilderImpl
    }

    @InstallIn(ActivityComponent::class)
    @Module
    object ConfirmIdentityAnotherWayJourneyControllerModule {
        @ActivityScoped
        @Provides
        fun providesController(
            router: ConfirmIdentityAnotherWayJourneyControllerImpl,
        ): ConfirmIdentityAnotherWayJourneyController = router
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object AbortJourneyApiModule {
        @Provides
        @ViewModelScoped
        fun providesAbortJourneyApiCall(
            abortJourneyApiCall: AbortJourneyApiCallImpl,
        ): AbortJourneyApiCall = abortJourneyApiCall
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object HandbackModule {
        @Provides
        @ViewModelScoped
        fun providesHandback(handback: HandbackImpl): Handback = handback
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object ActivityResultRegistryModule {
        @Provides
        @ActivityScoped
        fun providesActivityResultRegistry(
            @ActivityContext
            context: Context,
        ): ActivityResultRegistry = (context as AppCompatActivity).activityResultRegistry
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object SupportButtonUriModule {
        @Provides
        @ActivityScoped
        fun supportFormUri(
            featureFlags: FeatureFlags,
            supportFormUriV1: SupportFormUriV1,
            supportFormUriV2: SupportFormUriV2,
        ): SupportFormUri =
            if (featureFlags[SUPPORT_BUTTON]) {
                supportFormUriV2
            } else {
                supportFormUriV1
            }
    }
}
