package uk.gov.onelogin.network.http

import io.ktor.client.HttpClient

interface IHttpClient {
    fun client(): HttpClient
}
