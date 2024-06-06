package uk.gov.onelogin.login.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface UseCaseModule {
    @Binds
    fun bindHandleLogin(useCase: HandleLoginImpl): HandleLogin

    @Binds
    fun bindVerifyIdToken(usecase: VerifyIdTokenImpl): VerifyIdToken
}
