package uk.gov.onelogin.core.tokens

import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class RefreshExchangeApiResponseTest {
    private val json = Json
    private val sut = RefreshExchangeApiResponse(
        tokenType = "test",
        accessToken = "test",
        expiresIn = 100,
        refreshToken = "test"
    )
    private val expectedEncoded = "{\"tokenType\":\"test\",\"accessToken\":\"test\"," +
        "\"expiresIn\":100,\"refreshToken\":\"test\"}"

    @Test
    fun serialisationSuccess() {
        assertEquals(
            expectedEncoded,
            json.encodeToJsonElement(sut).toString()
        )
    }

    @Test
    fun deserialisationSuccess() {
        assertEquals(
            sut,
            json.decodeFromString<RefreshExchangeApiResponse>(expectedEncoded)
        )
    }
}
