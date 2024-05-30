package uk.gov.onelogin.tokens.verifier

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import org.jose4j.jwk.JsonWebKey
import org.jose4j.lang.JoseException

class JwkDeserializer : JsonDeserializer<JsonWebKey> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): JsonWebKey {
        val jwkParameters: Map<String, Any> = context?.deserialize(
            json,
            LinkedHashMap::class.java
        ) ?: throw IllegalArgumentException("JsonDeserializationContext is null")
        return try {
            JsonWebKey.Factory.newJwk(jwkParameters)
        } catch (e: JoseException) {
            throw JsonParseException("Unable to create JWK Object when parsing JSON", e)
        }
    }
}
