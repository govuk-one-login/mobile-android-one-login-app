package uk.gov.onelogin.core.utils

import androidx.core.net.toUri
import javax.inject.Inject

class AndroidUriParser
    @Inject
    constructor() : UriParser {
        override fun parse(uri: String) = uri.toUri()
    }
