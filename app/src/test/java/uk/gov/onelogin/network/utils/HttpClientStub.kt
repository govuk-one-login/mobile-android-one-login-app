package uk.gov.onelogin.network.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import junit.framework.AssertionFailedError

class HttpClientStub {
    private val responses: MutableMap<Url, MutableList<HttpClientStubResponse>> = mutableMapOf()
    private val calls: MutableMap<Url, Int> = mutableMapOf()

    val client = HttpClient(
        MockEngine {
            if (!responses.containsKey(it.url)) {
                throw AssertionFailedError("No mock response found for ${it.url}")
            }
            val responses = responses.get(it.url)

            if (responses.isNullOrEmpty()) {
                throw AssertionFailedError("Expected a mock response for ${it.url} but none were left")
            }

            val response = responses.removeFirst()

            calls[it.url] = calls.getOrDefault(it.url, 0) + 1

            respond(
                content = response.content,
                headers = response.headers,
                status = response.status
            )
        }
    )

    fun addResponse(url: Url, response: HttpClientStubResponse) {
        val responseList = responses.getOrDefault(url, mutableListOf())

        responseList.add(response)

        responses[url] = responseList
    }

    fun callsRemaining(): Int {
        return if (responses.isEmpty()) {
            0
        } else {
            responses.map {
                it.value.size
            }.reduce { acc, size ->
                acc + size
            }
        }
    }

    companion object {
        data class HttpClientStubResponse(
            val content: String,
            val headers: Headers,
            val status: HttpStatusCode
        )
    }
}
