package uk.gov.onelogin.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreIdImpl

@Module
@InstallIn(ViewModelComponent::class)
interface ValidateWalletStoreIdModule {
    @Binds
    fun bindsValidateWalletStoreId(validateWalletStoreIdImpl: ValidateWalletStoreIdImpl): ValidateWalletStoreId
}
