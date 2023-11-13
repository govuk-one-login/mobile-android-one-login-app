package uk.gov.onelogin.network.utils

class OnlineCheckerStub : IOnlineChecker {
    var online: Boolean = true

    override fun isOnline(): Boolean = online
}
