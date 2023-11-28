package uk.gov.onelogin.login.authentication

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun providesLoginSession(
        @ApplicationContext
        context: Context
    ): ILoginSession = LoginSession()
}
