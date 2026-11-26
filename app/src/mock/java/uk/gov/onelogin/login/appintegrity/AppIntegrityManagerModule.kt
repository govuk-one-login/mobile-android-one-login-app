package uk.gov.onelogin.login.appintegrity

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.FirebaseAppIntegrityManager
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityDevOptionsRepository
import uk.gov.onelogin.features.login.domain.appintegrity.DevAppIntegrityManager

@Module
@InstallIn(ViewModelComponent::class)
object AppIntegrityManagerModule {
    @Provides
    fun provideAppIntegrityManager(
        devOptions: AppIntegrityDevOptionsRepository,
        manager: FirebaseAppIntegrityManager,
    ): AppIntegrityManager = DevAppIntegrityManager(devOptions, manager)
}
