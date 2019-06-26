package douglasspgyn.com.github.maximatechtrainingserver.security.jwt

import com.auth0.jwt.interfaces.Payload
import io.ktor.auth.Principal

const val ROLE_USER: String = "user"
const val ROLE_ADMIN: String = "admin"

data class JWTPayload(private val payload: Payload) : Principal {
    val id: Long = payload.getClaim("id").asLong()
    val role: String = payload.getClaim("role").asString()
}