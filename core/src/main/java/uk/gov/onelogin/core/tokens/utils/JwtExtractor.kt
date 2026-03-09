package uk.gov.onelogin.core.tokens.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption

interface JwtExtractor {
    fun extractString(
        jwt: String,
        valueToExtract: String
    ): String?
}

class JwtExtractorImpl : JwtExtractor {
    override fun extractString(
        jwt: String,
        valueToExtract: String
    ): String? {
        val bodyEncoded = jwt.split(".")[1]
        val body =
            String(Base64.withPadding(PaddingOption.PRESENT_OPTIONAL).decode(bodyEncoded))
        val data = Json.parseToJsonElement(body)
        val extractedDataJson = data.jsonObject[valueToExtract]
        val extractedData = extractedDataJson?.toString()?.removeSurrounding("\"")
        return extractedData
    }
}
