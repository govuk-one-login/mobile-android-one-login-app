package uk.gov.onelogin.login

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.login.LoginSession
import uk.gov.onelogin.libraries.mocknetwork.auth.MockLoginSession

/**
 * Provides a mocked [LoginSession] in place of the AppAuth implementation.
 *
 * Login cannot be mocked by the mocked network layer, because AppAuth opens a browser and uses
 * its own HTTP stack rather than the app's network service.
 */
@Module
@InstallIn(SingletonComponent::class)
object LoginSessionModule {
    @Provides
    fun providesLoginSession(
        @ApplicationContext
        context: Context,
    ): LoginSession = MockLoginSession(context)
}
