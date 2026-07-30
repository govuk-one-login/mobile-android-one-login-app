package uk.gov.onelogin.features.appinfo.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.service.NetworkingException
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.android.network.service.v2.NetworkServiceTypedSuccessExt.makeRequest
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
        override suspend fun callApi(): ApiResponse<AppInfoData, String, NetworkingException> {
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
            return networkService.makeRequest<AppInfoData>(request)
        }
    }
