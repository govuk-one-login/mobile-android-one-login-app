package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.tokens.usecases.GetEmailImpl
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStore
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.GetPersistentIdImpl
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.tokens.usecases.GetTokenExpiryImpl
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreDataImpl
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiryImpl
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiryImpl
import uk.gov.onelogin.tokens.verifier.Jose4jJwtVerifier
import uk.gov.onelogin.tokens.verifier.JwtVerifier

@Module
@InstallIn(ViewModelComponent::class)
interface TokenModule {
    @Binds
    fun bindGetFromSecureStore(
        getFromSecureStore: GetFromTokenSecureStoreImpl
    ): GetFromTokenSecureStore

    @Binds
    fun bindSaveToSecureStore(
        saveToSecureStore: SaveToSecureStoreImpl
    ): SaveToSecureStore

    @Binds
    fun bindSaveToOpenSecureStore(
        saveToOpenSecureStore: SaveToOpenSecureStoreImpl
    ): SaveToOpenSecureStore

    @Binds
    fun bindClearAllSecureStore(
        clearAllSecureStore: RemoveAllSecureStoreDataImpl
    ): RemoveAllSecureStoreData

    @Binds
    fun bindRemoveTokenExpiry(
        removeTokenExpiry: RemoveTokenExpiryImpl
    ): RemoveTokenExpiry

    @Binds
    fun bindGetTokenExpiry(
        getTokenExpiry: GetTokenExpiryImpl
    ): GetTokenExpiry

    @Binds
    fun bindGetEmail(
        getEmail: GetEmailImpl
    ): GetEmail

    @Binds
    fun bindGetPersistentId(
        getPersistentId: GetPersistentIdImpl
    ): GetPersistentId

    @Binds
    fun bindSaveTokenExpiry(
        saveTokenExpiry: SaveTokenExpiryImpl
    ): SaveTokenExpiry

    @Binds
    fun bindJwtVerifier(verifier: Jose4jJwtVerifier): JwtVerifier
}
