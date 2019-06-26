package douglasspgyn.com.github.maximatechtrainingserver.security.jwt

import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt

fun Authentication.Configuration.jwtAuthentication(name: String?) {
    jwt(name) {
        verifier(JwtConfig.accessTokenVerifier)
        validate { credential ->
            JWTPayload(credential.payload)
        }
    }
}