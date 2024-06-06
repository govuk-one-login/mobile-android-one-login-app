package uk.gov.onelogin.login.usecase

import android.util.Log
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.tokens.verifier.JwtVerifier

interface VerifyIdToken {
    operator fun invoke(
        idToken: String,
        jwksUrl: String,
        callback: (Boolean) -> Unit
    )
}

@Suppress("TooGenericExceptionCaught")
class VerifyIdTokenImpl @Inject constructor(
    private val httpClient: GenericHttpClient,
    private val verifier: JwtVerifier
) : VerifyIdToken {
    override fun invoke(
        idToken: String,
        jwksUrl: String,
        callback: (Boolean) -> Unit
    ) {
        MainScope().launch {
            var verified = false
            val response = httpClient.makeRequest(
                ApiRequest.Get(jwksUrl)
            )

            if (response is ApiResponse.Success<*>) {
                try {
                    getKeyId(idToken)?.let { keyId ->
                        getKey(keyId, response.response.toString())?.let { key ->
                            verified = verifier.verify(
                                idToken,
                                key
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(this::class.simpleName, e.message, e)
                }
            }

            callback(verified)
        }
    }

    private fun getKey(keyId: String, jwksResponse: String): String? {
        try {
            val data = Json.parseToJsonElement(jwksResponse)
            data.jsonObject["keys"]?.jsonArray?.forEach {
                if (it.jsonObject["kid"].toString() == keyId) {
                    return it.jsonObject.toString()
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.simpleName, e.message, e)
        }
        return null
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getKeyId(idToken: String): String? {
        try {
            val headerEncoded = idToken.split(".")[0]
            val header = String(Base64.decode(headerEncoded))
            val data = Json.parseToJsonElement(header)
            return data.jsonObject["kid"].toString()
        } catch (e: Exception) {
            Log.e(this::class.simpleName, e.message, e)
            return null
        }
    }
}
