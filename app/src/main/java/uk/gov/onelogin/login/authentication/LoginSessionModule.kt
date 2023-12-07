package uk.gov.onelogin.login.authentication

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.ILoginSession
import uk.gov.android.authentication.LoginSession
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoginSessionModule @Inject constructor() {

    @Provides
    @Singleton
    fun providesLoginSession(): ILoginSession = LoginSession()
}
