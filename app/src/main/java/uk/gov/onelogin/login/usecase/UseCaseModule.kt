package uk.gov.onelogin.login.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface UseCaseModule {
    @Binds
    fun bindHandleLogin(useCase: HandleLocalLoginImpl): HandleLocalLogin

    @Binds
    fun bindVerifyIdToken(usecase: VerifyIdTokenImpl): VerifyIdToken

    @Binds
    fun bindSaveTokens(saveTokens: SaveTokensImpl): SaveTokens

    @Binds
    fun bindHandleRemoteLogin(handleLogin: HandleRemoteLoginImpl): HandleRemoteLogin

    @Binds
    fun bindHandleLoginRedirect(handleRedirect: HandleLoginRedirectImpl): HandleLoginRedirect
}
