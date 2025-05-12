package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreDataImpl
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiryImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetEmail
import uk.gov.onelogin.core.tokens.domain.retrieve.GetEmailImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStoreImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromOpenSecureStoreImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentIdImpl
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentIdImpl
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStoreImpl
import uk.gov.onelogin.core.tokens.domain.save.SaveToTokenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveToTokenSecureStoreImpl
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface TokenModule {
    @Binds
    fun bindGetFromTokenSecureStore(getFromSecureStore: GetFromEncryptedSecureStoreImpl): GetFromEncryptedSecureStore

    @Binds
    fun bindGetFromOpenSecureStore(getFromOpenSecureStore: GetFromOpenSecureStoreImpl): GetFromOpenSecureStore

    @Binds
    fun bindSaveToSecureStore(saveToSecureStore: SaveToTokenSecureStoreImpl): SaveToTokenSecureStore

    @Binds
    fun bindSaveToOpenSecureStore(saveToOpenSecureStore: SaveToOpenSecureStoreImpl): SaveToOpenSecureStore

    @Binds
    fun bindClearAllSecureStore(clearAllSecureStore: RemoveAllSecureStoreDataImpl): RemoveAllSecureStoreData

    @Binds
    fun bindRemoveTokenExpiry(removeTokenExpiry: RemoveTokenExpiryImpl): RemoveTokenExpiry

    @Binds
    fun bindGetEmail(getEmail: GetEmailImpl): GetEmail

    @Binds
    fun bindSavePersistentId(saveId: SavePersistentIdImpl): SavePersistentId

    @Binds
    fun bindGetPersistentId(getPersistentId: GetPersistentIdImpl): GetPersistentId

    @Binds
    fun bindSaveTokenExpiry(saveTokenExpiry: SaveTokenExpiryImpl): SaveTokenExpiry
}
