package uk.gov.onelogin.features.login.domain.validateWalletStoreId

import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import javax.inject.Inject

/** Returns `true` if a wallet store ID is present */
class ValidateWalletStoreId
    @Inject
    constructor(
        private val getWalletStoreId: GetWalletStoreId
    ) {
        suspend operator fun invoke(): Boolean = getWalletStoreId() != null
    }
