package shop.tsrecipe.gateway.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import org.springframework.stereotype.Service
import shop.tsrecipe.gateway.exception.BaseException
import shop.tsrecipe.gateway.exception.ErrorCode
import shop.tsrecipe.gateway.properties.JwtProperties

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
    private val algorithm by lazy { Algorithm.HMAC256(jwtProperties.secretKey) }

    private val verifier: JWTVerifier by lazy {
        JWT.require(algorithm)
            .withIssuer(jwtProperties.issuer)
            .build()
    }

    fun verifyToken(token: String): String {
        return try {
            verifier.verify(token).subject
        } catch (e: JWTVerificationException) {
            val cause = if (e is TokenExpiredException) ErrorCode.TOKEN_EXPIRED else ErrorCode.INVALID_TOKEN
            throw BaseException(cause)
        }
    }
}