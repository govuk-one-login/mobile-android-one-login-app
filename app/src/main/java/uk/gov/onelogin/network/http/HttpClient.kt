package uk.gov.onelogin.network.http

import io.ktor.client.HttpClient

class HttpClient : IHttpClient {
    private val _client = HttpClient()

    override fun client(): HttpClient = _client
}
