package uk.gov.onelogin.tokens.verifier

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature

/**
 * Validate a signed JSON Web Token (JWT) using jose4j library
 */
class Jose4jJwtVerifier : JwtVerifier {
    /**
     * Validate a signed JWT
     *
     * @param encodedJsonWebToken the encoded JWT
     * @param publicKeyJwkString public key in JSON Web Key (JWK) format
     * @throws JsonParseException when the JWK parse fails due to invalid JSON
     * @throws IllegalArgumentException when the JWK parse fails due to null context
     * @throws JoseException when the encoded JWT is invalid
     */
    override fun verify(encodedJsonWebToken: String, publicKeyJwkString: String): Boolean {
        val gson = GsonBuilder()
            .registerTypeAdapter(JsonWebKey::class.java, JwkDeserializer())
            .create()

        val publicKeyJwk = gson.fromJson(publicKeyJwkString, JsonWebKey::class.java)
        val jws = JsonWebSignature()
        jws.setAlgorithmConstraints(
            AlgorithmConstraints(
                AlgorithmConstraints.ConstraintType.PERMIT,
                AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256
            )
        )
        jws.compactSerialization = encodedJsonWebToken
        jws.key = publicKeyJwk.key

        return jws.verifySignature()
    }
}
