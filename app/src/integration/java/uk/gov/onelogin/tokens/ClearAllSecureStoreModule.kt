package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreDataImpl

@Module
@InstallIn(ViewModelComponent::class)
interface ClearAllSecureStoreModule {
    @Binds
    fun bindClearAllSecureStore(
        clearAllSecureStore: RemoveAllSecureStoreDataImpl
    ): RemoveAllSecureStoreData
}
