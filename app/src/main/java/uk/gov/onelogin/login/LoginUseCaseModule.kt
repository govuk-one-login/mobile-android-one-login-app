package uk.gov.onelogin.login

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.remove.RemoveRefreshTokenAndExpiry
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.SaveTokensImpl
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLoginImpl
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@InstallIn(ViewModelComponent::class)
@Module
interface LoginUseCaseModule {
    @Binds
    fun bindHandleLogin(useCase: HandleLocalLoginImpl): HandleLocalLogin

    @Binds
    fun bindSaveTokens(saveTokens: SaveTokensImpl): SaveTokens

    @Binds
    fun bindHandleRemoteLogin(handleLogin: StartRemoteLoginImpl): StartRemoteLogin

    @Binds
    fun bindHandleLoginRedirect(handleRedirect: FinaliseRemoteLoginImpl): FinaliseRemoteLogin
}

@InstallIn(ViewModelComponent::class)
@Module
object LoginUseCaseObjectModule {
    @Suppress("LongParameterList")
    @Provides
    fun bindRemoteLogin(
        @ApplicationContext
        context: Context,
        finaliseRemoteLogin: FinaliseRemoteLogin,
        startRemoteLogin: StartRemoteLogin,
        localAuthManager: LocalAuthManager,
        tokenRepository: TokenRepository,
        verifyIdToken: VerifyIdToken,
        autoInitialiseSecureStore: AutoInitialiseSecureStore,
        savePersistentId: SavePersistentId,
        saveTokenExpiry: SaveTokenExpiry,
        signOutUseCase: SignOutUseCase,
        removeRefreshTokenAndExpiry: RemoveRefreshTokenAndExpiry,
        errorCounter: Counter,
        logger: Logger,
        navigator: Navigator
    ): RemoteLogin =
        RemoteLoginImpl(
            context = context,
            finaliseRemoteLogin = finaliseRemoteLogin,
            startRemoteLogin = startRemoteLogin,
            localAuthManager = localAuthManager,
            tokenRepository = tokenRepository,
            verifyIdToken = verifyIdToken,
            autoInitialiseSecureStore = autoInitialiseSecureStore,
            savePersistentId = savePersistentId,
            saveTokenExpiry = saveTokenExpiry,
            signOutUseCase = signOutUseCase,
            removeRefreshTokenAndExpiry = removeRefreshTokenAndExpiry,
            errorCounter = errorCounter,
            logger = logger,
            navigator = navigator
        )
}
