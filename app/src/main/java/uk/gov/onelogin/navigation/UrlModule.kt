package uk.gov.onelogin.navigation

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.urls.GovUkSignInUrl

@Module
@InstallIn(ViewModelComponent::class)
object UrlModule {
    @Provides
    @GovUkSignInUrl
    fun provideGovUkSignInUrl(@ApplicationContext context: Context): String =
        context.getString(R.string.app_govUkSignInUrl)
}
