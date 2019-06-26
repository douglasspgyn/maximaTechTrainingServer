package douglasspgyn.com.github.maximatechtrainingserver.security.basic

import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.UnauthorizedThrowable
import io.ktor.application.call
import io.ktor.auth.*
import java.util.*

private const val basicAuth: String = "BasicAuth"

class BasicAuthenticationProvider(name: String?) : AuthenticationProvider(name) {
    internal var basicOptions: HashMap<String, String> = hashMapOf()
    internal var basicSeparator: String = ":"

    fun basicList(basicOptions: HashMap<String, String>) {
        this.basicOptions = basicOptions
    }

    fun basicSeparator(basicSeparator: String) {
        this.basicSeparator = basicSeparator
    }
}

fun Authentication.Configuration.basic(name: String? = null, configure: BasicAuthenticationProvider.() -> Unit) {
    val provider = BasicAuthenticationProvider(name).apply(configure)
    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val authorization = call.request.headers["Authorization"]
        validateBasic(authorization, provider.basicOptions, provider.basicSeparator, context)
    }
    register(provider)
}

private suspend fun validateBasic(authorization: String?, basicOptions: HashMap<String, String>,
                                  basicSeparator: String, context: AuthenticationContext) {
    val authorizationItems = authorization?.split(" ")
    var clientType = ""
    val authorized = authorizationItems?.let {
        if (it.size == 2 && it[0] == "Basic") {
            val basicItems = String(Base64.getDecoder().decode(it[1])).split(basicSeparator)
            if (basicItems.size == 2) {
                clientType = basicItems[0]
                basicOptions[clientType] == basicItems[1]
            } else {
                false
            }
        } else {
            false
        }
    } ?: false

    if (!authorized) {
        abort(context)
    } else {
        context.principal(BasicPayload(clientType))
    }
}

private fun abort(context: AuthenticationContext) {
    context.challenge(basicAuth, AuthenticationFailedCause.NoCredentials) {
        throw UnauthorizedThrowable()
    }
}