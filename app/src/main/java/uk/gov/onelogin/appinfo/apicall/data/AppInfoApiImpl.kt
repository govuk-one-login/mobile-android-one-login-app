package uk.gov.onelogin.appinfo.apicall.data

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.R
import uk.gov.onelogin.appinfo.apicall.domain.AppInfoApi
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import javax.inject.Inject

class AppInfoApiImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient,
    private val onlineChecker: OnlineChecker
) : AppInfoApi {
    private val jsonDecoder = Json { ignoreUnknownKeys = true }

    override suspend fun callApi(): ApiResponse {
        if (!onlineChecker.isOnline()) {
            return ApiResponse.Offline
        }
        val endpoint = context.getString(R.string.appInfoEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.appInfoUrl, endpoint),
            headers = listOf(
                "Cache-Control" to "no-store",
                "Content-Type" to "application/json",
                "Strict-Transport-Security" to "max-age=31536000",
                "X-Content-Type-Options" to "nosniff",
                "X-Frame-Options" to "DENY"
            )
        )
        return when (val response = httpClient.makeRequest(request)) {
            is ApiResponse.Success<*> -> {
                val decodedResponse = jsonDecoder
                    .decodeFromString<AppInfoData>(response.response.toString())
                Log.d("AppInfo", "$decodedResponse")
                ApiResponse.Success(decodedResponse)
            }
            else -> response
        }
    }
}
