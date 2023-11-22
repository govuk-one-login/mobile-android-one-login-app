package uk.gov.onelogin.network.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class OnlineChecker constructor(
    private val connectivityManager: ConnectivityManager
) : IOnlineChecker {

    private val applicableTransportTypes = arrayOf(
        NetworkCapabilities.TRANSPORT_CELLULAR,
        NetworkCapabilities.TRANSPORT_WIFI,
        NetworkCapabilities.TRANSPORT_ETHERNET
    )

    /**
     * Check whether the User's mobile device currently has access to the Internet.
     *
     * @return true when the device has mobile data, Wi-Fi or Ethernet capabilities.
     */
    override fun isOnline(): Boolean {
        return connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.let { networkCapabilities ->
            applicableTransportTypes.any { transportType ->
                networkCapabilities.hasTransport(transportType)
            }
        } ?: false
    }
}
