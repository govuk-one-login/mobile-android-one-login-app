package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.tokens.domain.idtoken.email.ExtractEmail
import uk.gov.onelogin.core.tokens.domain.idtoken.email.ExtractEmailImpl
import uk.gov.onelogin.core.tokens.domain.idtoken.iss.ExtractAndVerifyIssuer
import uk.gov.onelogin.core.tokens.domain.idtoken.iss.ExtractAndVerifyIssuerImpl
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.ExtractAndSaveWalletId
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.ExtractAndSaveWalletIdImpl
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.remove.RemoveTokenExpiryImpl
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
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface TokenModule {
    @Binds
    fun bindGetFromTokenSecureStore(getFromSecureStore: GetFromEncryptedSecureStoreImpl): GetFromEncryptedSecureStore

    @Binds
    fun bindSaveToSecureStore(saveToSecureStore: SaveToTokenSecureStoreImpl): SaveToTokenSecureStore

    @Binds
    fun bindSaveToOpenSecureStore(saveToOpenSecureStore: SaveToOpenSecureStoreImpl): SaveToOpenSecureStore

    @Binds
    fun bindRemoveTokenExpiry(removeTokenExpiry: RemoveTokenExpiryImpl): RemoveTokenExpiry

    @Binds
    fun bindGetEmail(extractEmailImpl: ExtractEmailImpl): ExtractEmail

    @Binds
    fun bindGetAndSaveWalletId(extractAndSaveWalletIdImpl: ExtractAndSaveWalletIdImpl): ExtractAndSaveWalletId

    @Binds
    fun bindExtractAndVerifyIss(extractAndVerifyIssuerImpl: ExtractAndVerifyIssuerImpl): ExtractAndVerifyIssuer

    @Binds
    fun bindSavePersistentId(saveId: SavePersistentIdImpl): SavePersistentId

    @Binds
    fun bindGetPersistentId(getPersistentId: GetPersistentIdImpl): GetPersistentId

    @Binds
    fun bindSaveTokenExpiry(saveTokenExpiry: SaveTokenExpiryImpl): SaveTokenExpiry
}

@Module
@InstallIn(SingletonComponent::class)
interface OpenStoreModule {
    @Binds
    fun bindGetFromOpenSecureStore(getFromOpenSecureStore: GetFromOpenSecureStoreImpl): GetFromOpenSecureStore
}
