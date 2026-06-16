package uk.gov.onelogin.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId

@Module
@InstallIn(ViewModelComponent::class)
object ValidateWalletStoreIdModule {
    @Provides
    fun providesValidateWalletStoreId(
        getWalletStoreId: GetWalletStoreId,
        getPersistentId: GetPersistentId,
        logger: Logger,
    ): ValidateWalletStoreId = ValidateWalletStoreId(getWalletStoreId, getPersistentId, logger)
}
