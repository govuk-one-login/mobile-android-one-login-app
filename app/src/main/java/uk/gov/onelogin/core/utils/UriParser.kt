package uk.gov.onelogin.core.utils

import android.net.Uri
import javax.inject.Inject

fun interface UriParser {
    fun parse(uri: String): Uri
}

class AndroidUriParser @Inject constructor() : UriParser {
    override fun parse(uri: String) =
        Uri.parse(uri)
}
