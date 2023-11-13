package uk.gov.onelogin.network.utils

/**
 * Abstraction for defining whether a User's device is online.
 */
interface IOnlineChecker {
    /**
     * Check whether the User's mobile device currently has access to the Internet.
     *
     * @return true when it's possible to perform external API calls.
     */
    fun isOnline(): Boolean
}
