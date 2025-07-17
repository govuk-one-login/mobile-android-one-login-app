package uk.gov.onelogin.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.VerifyIdTokenImpl

@InstallIn(ViewModelComponent::class)
@Module
interface VerifyIdModule {
    @Binds
    fun bindVerifyIdToken(usecase: VerifyIdTokenImpl): VerifyIdToken
}
