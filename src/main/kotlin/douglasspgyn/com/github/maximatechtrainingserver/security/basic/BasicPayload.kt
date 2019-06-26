package douglasspgyn.com.github.maximatechtrainingserver.security.basic

import io.ktor.auth.Principal

data class BasicPayload(val clientType: String) : Principal