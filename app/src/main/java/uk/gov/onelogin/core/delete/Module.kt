package uk.gov.onelogin.core.delete

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.Dispatchers
import uk.gov.onelogin.core.delete.domain.Cleaner
import uk.gov.onelogin.core.delete.domain.MultiCleaner
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData

@Module
@InstallIn(ViewModelComponent::class)
internal object Module {
    @Provides
    fun provideCleaner(
        optInRepository: OptInRepository,
        biometricPreferenceHandler: BiometricPreferenceHandler,
        secureStoreData: RemoveAllSecureStoreData,
    ): Cleaner = MultiCleaner(
        Dispatchers.Default,
        optInRepository,
        biometricPreferenceHandler,
        secureStoreData
    )
}
