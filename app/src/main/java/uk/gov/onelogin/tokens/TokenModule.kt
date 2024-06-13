package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetFromSecureStoreImpl
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.tokens.usecases.GetTokenExpiryImpl
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
        getFromSecureStore: GetFromSecureStoreImpl
    ): GetFromSecureStore

    @Binds
    fun bindSaveToSecureStore(
        saveToSecureStore: SaveToSecureStoreImpl
    ): SaveToSecureStore

    @Binds
    fun bindGetTokenExpiry(
        getTokenExpiry: GetTokenExpiryImpl
    ): GetTokenExpiry

    @Binds
    fun bindGetEmail(
        tokenRepository: TokenRepository
    ): GetEmail

    @Binds
    fun bindSaveTokenExpiry(
        saveTokenExpiry: SaveTokenExpiryImpl
    ): SaveTokenExpiry

    @Binds
    fun bindJwtVerifier(verifier: Jose4jJwtVerifier): JwtVerifier
}
