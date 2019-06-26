package douglasspgyn.com.github.maximatechtrainingserver.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import java.util.*

object JwtConfig {

    const val tokenType: String = "Bearer"
    //Access Token
    private const val secretAccessToken: String = "62llGixAlHC0M5TvdsfqXCJJO16HrXiMZQu7dBCWJe3uOVc9CoMToyS2OmaHjj9r9LcIxPiQpwnUhhDaG6V0Ob4lxB2OsH88c26Ef9FmbRoOvrT2ZHE2rNR6L8n83Z5ESjCO8rExM8NKGTLjcUmrqsjWWvINxzj2lIcSleVBtH57WpimKgI3Zk2WlZlBfbM0CiqkZr9PP5iss8G61MQMuykOhFGEInyHPijhe8oSDs4SpGbIGgGF0qVIfHOGnqT64SnyMV3AiIUo5nuG6WT4EJplY0aQS5i8yOD4iGgapA9Pu4CB4yqOVWSPa4vL8DRtfv9u626MaTa3AFnpPTh2T6PAxqnMg9Q3P0MXBv4dyzU8iUYzZhHKlEo4pEFMLq7fV7ttSRM799cUWghaND4EwhXssJwPvhyI5cGPAto6Utkuw2ZUMGbwNSe3WXmYeXyWZkmA4spVAPdF4oxDLrWxieyaKWvkaJHw9b1EMIQdq09wRLvit7yMBshsBThLT8tK"
    private const val accessTokenValidityInMs: Long = 30000 // 30 seconds
    private val accessTokenAlgorithm: Algorithm = Algorithm.HMAC512(secretAccessToken)
    //Refresh Token
    private const val secretRefreshToken: String = "B44u7mpS69GXinMOyl7LuIFuPWc9W4zblPZvKCzDAqF3diZBn2lxlrqNBg0PEYi6r0GGlwEZy01rh9oEenqUKYGKovMubhxaZRkesI0NRv3S9oO2sh3FPTwbVtshC2PS3XbDPq5Iyrq4mgzYDu9NebwPvBBlHczQkAhxz1KWkbRjW21IBWKOif9woZpMe9REklT4QudiIAGqNonKBD4jNh47EWw8kWpBWX1VAgfbzriycTyAntw5BuLWTzEb2bUTxEok4kHH8cJaRcq6XuzmooEuPYESUOSmad1s1lkX0hyJub29R9E21hKXRw1bnNYZRU8dpcqlKhYlZGN2D758mwn7ruFwLAGzDcLojgQRn2MNARIXHx0Sw0k8INRBSOFjwl8OVgT9iZi14lr4pOm7XUnxQZ1w9W3QJlzk2sHobKQgpOyW5q0fUwWtBUnhima8DyqmtjsRc1s8sw05jFoDt6WFIyc7RFC2vUHf0447WDAKZfkMAVQQKD6KXVTr1VgS"
    private const val refreshTokenValidityInMs: Long = 60000 // 60 seconds
    private val refreshTokenAlgorithm: Algorithm = Algorithm.HMAC512(secretRefreshToken)

    /**
     * Create a token (JWT) verifier
     */
    val accessTokenVerifier: JWTVerifier = JWT
            .require(accessTokenAlgorithm)
            .build()

    val refreshTokenVerifier: JWTVerifier = JWT
            .require(refreshTokenAlgorithm)
            .build()

    /**
     * Generate a token
     */
    fun makeAccessToken(user: User, clientType: String): String {
        return JWT.create()
                .withClaim("id", user.id)
                .withClaim("client_type", clientType)
                .withClaim("role", user.role)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(getAccessTokenExpiration())
                .sign(accessTokenAlgorithm)
    }

    fun makeRefreshToken(user: User, accessToken: DecodedJWT, clientType: String): String {
        return JWT.create()
                .withClaim("id", user.id)
                .withClaim("client_type", clientType)
                .withClaim("role", user.role)
                .withClaim("aid", accessToken.getClaim("jti").asString())
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(getRefreshTokenExpiration())
                .sign(refreshTokenAlgorithm)
    }

    /**
     * Calculate the expiration date based on current time + the given validity
     */
    private fun getAccessTokenExpiration() = Date(System.currentTimeMillis() + accessTokenValidityInMs)

    private fun getRefreshTokenExpiration() = Date(System.currentTimeMillis() + refreshTokenValidityInMs)
}