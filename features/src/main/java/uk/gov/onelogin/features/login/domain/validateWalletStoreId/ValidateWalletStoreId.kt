package uk.gov.onelogin.features.login.domain.validateWalletStoreId

/**
 * Returns `true` if a wallet store ID is present.
 * Checks that the wallet store ID is present, given that the user is already signed in.
 *
 * Validates that persistentId is present before retrieving wallet store ID from secure storage.
 * The wallet store ID must be saved before using the wallet.
 *
 * Since One Login app v1.17.0, the user's wallet store ID is saved at the point when a
 * user is signed in. However, users who signed in using a previous app version will not
 * have a wallet store ID.
 * [ValidateWalletStoreId] is just for detecting those users. We can then retrieve a
 * wallet store ID for them, migrating their session to the proper state.
 */
fun interface ValidateWalletStoreId {
    suspend operator fun invoke(): Boolean
}
