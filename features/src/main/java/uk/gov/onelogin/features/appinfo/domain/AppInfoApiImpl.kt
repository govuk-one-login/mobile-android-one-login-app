package uk.gov.onelogin.features.appinfo.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v2.ApiResponse
import uk.gov.android.network.service.NetworkService
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import javax.inject.Inject

class AppInfoApiImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
        private val networkService: NetworkService,
    ) : AppInfoApi {
        private val jsonDecoder = Json { ignoreUnknownKeys = true }

        override suspend fun callApi(): AppInfoApiResult {
            val endpoint = context.getString(R.string.appInfoEndpoint)
            val request =
                ApiRequest.Get(
                    url = context.getString(R.string.appInfoUrl, endpoint),
                    headers =
                        listOf(
                            "Cache-Control" to "no-store",
                            "Content-Type" to "application/json",
                            "Strict-Transport-Security" to "max-age=31536000",
                            "X-Content-Type-Options" to "nosniff",
                            "X-Frame-Options" to "DENY",
                        ),
                )
            return when (val response = networkService.makeRequest(request)) {
                is ApiResponse.Success ->
                    try {
                        val data = jsonDecoder.decodeFromString<AppInfoData>(response.response)
                        AppInfoApiResult.Success(data)
                    } catch (e: SerializationException) {
                        AppInfoApiResult.Failure(response.status, e)
                    }

                is ApiResponse.Failure ->
                    AppInfoApiResult.Failure(response.status ?: 0, response.error)
            }
        }
    }
