package uk.gov.onelogin.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.SaveTokensImpl
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLoginImpl
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLoginImpl

@InstallIn(ViewModelComponent::class)
@Module
interface LoginUseCaseModule {
    @Binds
    fun bindHandleLocalLogin(impl: HandleLocalLoginImpl): HandleLocalLogin

    @Binds
    fun bindSaveTokens(saveTokens: SaveTokensImpl): SaveTokens

    @Binds
    fun bindStartRemoteLogin(impl: StartRemoteLoginImpl): StartRemoteLogin

    @Binds
    fun bindFinaliseRemoteLogin(impl: FinaliseRemoteLoginImpl): FinaliseRemoteLogin

    @Binds
    fun bindRemoteLogin(impl: RemoteLoginImpl): RemoteLogin
}
