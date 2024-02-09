package uk.gov.onelogin.network.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject

class HttpClient @Inject constructor() : IHttpClient {
    private val _client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    override fun client(): HttpClient = _client
}
