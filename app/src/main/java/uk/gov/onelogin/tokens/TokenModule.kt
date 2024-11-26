package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.tokens.usecases.GetEmailImpl
import uk.gov.onelogin.tokens.usecases.GetFromOpenSecureStore
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStore
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.GetPersistentIdImpl
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreData
import uk.gov.onelogin.tokens.usecases.RemoveAllSecureStoreDataImpl
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiry
import uk.gov.onelogin.tokens.usecases.RemoveTokenExpiryImpl
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiryImpl
import uk.gov.onelogin.tokens.usecases.TemporaryGetFromOpenSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.TemporarySaveToOpenSecureStoreImpl
import uk.gov.onelogin.tokens.verifier.Jose4jJwtVerifier
import uk.gov.onelogin.tokens.verifier.JwtVerifier

@Module
@InstallIn(ViewModelComponent::class)
@Suppress("TooManyFunctions")
interface TokenModule {
    @Binds
    fun bindGetFromTokenSecureStore(
        getFromSecureStore: GetFromTokenSecureStoreImpl
    ): GetFromTokenSecureStore

    @Binds
    fun bindGetFromOpenSecureStore(
        getFromOpenSecureStore: TemporaryGetFromOpenSecureStoreImpl
    ): GetFromOpenSecureStore

    @Binds
    fun bindSaveToSecureStore(
        saveToSecureStore: SaveToSecureStoreImpl
    ): SaveToSecureStore

    @Binds
    fun bindSaveToOpenSecureStore(
        saveToOpenSecureStore: TemporarySaveToOpenSecureStoreImpl
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
