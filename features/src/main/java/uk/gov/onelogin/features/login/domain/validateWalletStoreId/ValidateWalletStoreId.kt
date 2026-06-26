package uk.gov.onelogin.features.login.domain.validateWalletStoreId

/**
 * Checks that the wallet store ID is present, given that the user is already signed in.
 *
 * The wallet store ID must be saved before using the wallet.
 *
 * Since One Login app v1.17.0, the user's wallet store ID is saved at the point when a
 * user is signed in. However, users who signed in using a previous app version will not
 * have a wallet store ID.
 * [ValidateWalletStoreId] is just for detecting those users. We can then retrieve a
 * wallet store ID for them, migrating their session to the proper state.
 *
 *  @return true if the wallet store ID is present, and false if not
 *  @throws AssertionError if this use case is invoked when the user is not signed (only in debug builds)
 */
fun interface ValidateWalletStoreId {
    suspend operator fun invoke(): Boolean
}
