package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface TokenModule {
    @Binds
    fun bindSaveToSecureStore(
        saveToSecureStore: SaveToSecureStoreImpl
    ): SaveToSecureStore

    @Binds
    fun bindSaveTokenExpiry(
        saveTokenExpiry: SaveTokenExpiryImpl
    ): SaveTokenExpiry
}
