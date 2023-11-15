package uk.gov.onelogin.network.http

import io.ktor.client.HttpClient
import javax.inject.Inject

class HttpClient @Inject constructor(): IHttpClient {
    private val _client = HttpClient()

    override fun client(): HttpClient = _client
}
