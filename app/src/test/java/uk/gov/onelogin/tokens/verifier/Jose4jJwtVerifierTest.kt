package uk.gov.onelogin.tokens.verifier

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class Jose4jJwtVerifierTest {
    private val encodedJwt = "eyJraWQiOiJkaWQ6d2ViOnRlc3Qtd3d3LnRheC5zZXJ2aWNlLmdvdi51ay9obXJjL" +
        "WdvdndhbGxldC1kaWQvI2tleS0wIiwiYWxnIjoiRVMyNTYiLCJ0eXAiOiJKV1QifQ.eyJleHAiOjE3MTE5" +
        "Mjk1OTksIkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sIn" +
        "N1YiI6InRlc3QgdXNlciBpZGVudGlmaWVyIiwibmJmIjoxNzAzMTUzNTg1LCJpc3MiOiJodHRwczovL3Rl" +
        "c3Qtd3d3LnRheC5zZXJ2aWNlLmdvdi51ay9obXJjLWdvdndhbGxldC1kaWQiLCJpYXQiOjE3MDMxNTM1OD" +
        "UsInZjIjp7InR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiLCJTb2NpYWxTZWN1cml0eUNyZWRlbnRp" +
        "YWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6W3sibmFtZVBhcnRzIjpbeyJ0eXBlIjoiVGl0bG" +
        "UiLCJ2YWx1ZSI6Ik1yIn0seyJ0eXBlIjoiR2l2ZW5OYW1lIiwidmFsdWUiOiJCb2IifSx7InR5cGUiOiJG" +
        "YW1pbHlOYW1lIiwidmFsdWUiOiJCb2JiaXR5In1dfV0sInNvY2lhbFNlY3VyaXR5UmVjb3JkIjpbeyJwZX" +
        "Jzb25hbE51bWJlciI6IlFRMDAwMDAzQiJ9XX19fQ.4ZZIyRIhG-N8WsfJRiIEYIbM7JG6uPU2ux255S8vm" +
        "MPWVIsBAedVooSAp43zB-H2ym1X4ymq51eRbJ2hw6KkvQ"

    private val jwk = "{\"crv\":\"P-256\",\"kid\":\"key-0\",\"kty\":\"EC\",\"x\":\"Shc8mJ6fcZik" +
        "hWM4ofHGSwXTkdqXM8GbPtRzPa7LttA=\",\"y\":\"OIhg/7rhWfmnWQEgAXzU8fCTggGrS3zj5x76a0l" +
        "rzJM=\"}"

    private val invalidJwk = "{\"crv\":\"P-256\",\"kid\":\"xuUZtjUzW2a-FgshCsLawbi08LL3aaHHhKKw" +
        "3w7O8x0\",\"kty\":\"EC\",\"x\":\"BY7gXGUvMrwrVuytSWVG4SAYD8dEYtUCdokR5q632xQ\",\"y" +
        "\":\"P3Zwqtz3XimgdwLEF-z7akHyiqAfsmfa5JfJlYHouZw\"}"

    private val sut = Jose4jJwtVerifier()

    @Test
    fun testWithValidJwtAndJwk() {
        val actualResult = sut.verify(encodedJwt, jwk)
        assertEquals(true, actualResult)
    }

    @Test
    fun testWithValidJwtAndInvalidJwk() {
        val actualResult = sut.verify(encodedJwt, invalidJwk)
        assertEquals(false, actualResult)
    }
}
