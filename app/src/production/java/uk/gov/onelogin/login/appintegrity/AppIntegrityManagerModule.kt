package uk.gov.onelogin.login.appintegrity

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.FirebaseAppIntegrityManager

@Module
@InstallIn(ViewModelComponent::class)
interface AppIntegrityManagerModule {
    @Binds
    fun bindAppIntegrityManager(manager: FirebaseAppIntegrityManager): AppIntegrityManager
}
