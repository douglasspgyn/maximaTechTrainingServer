package douglasspgyn.com.github.maximatechtrainingserver

import com.typesafe.config.ConfigFactory
import douglasspgyn.com.github.maximatechtrainingserver.controller.authentication.authenticationController
import douglasspgyn.com.github.maximatechtrainingserver.controller.health.healthController
import douglasspgyn.com.github.maximatechtrainingserver.controller.user.userController
import douglasspgyn.com.github.maximatechtrainingserver.security.basic.basicAuthentication
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.jwtAuthentication
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.throwableManager
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.text.DateFormat

private val dotEnv: Dotenv = dotenv {
    directory = "."
    ignoreIfMissing = true
}

private const val authTypeBasic: String = "basic"
private const val authTypeJwt: String = "jwt"

@KtorExperimentalAPI
fun main() {
    embeddedServer(Netty, applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            app()
        }

        connector {
            port = dotEnv["PORT"]!!.toInt()
        }
    }).start()
}

fun Application.app() {
    Database.connect(dotEnv["JDBC_DATABASE_URL"]!!,
            "org.postgresql.Driver")

    install(Authentication) {
        basicAuthentication(authTypeBasic)
        jwtAuthentication(authTypeJwt)
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(Compression)
    install(StatusPages) {
        throwableManager()
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(Routing) {
        route("/") {
            healthController()
        }

        authenticate(authTypeBasic) {
            route("/oauth") {
                authenticationController()
            }
        }

        authenticate(authTypeJwt) {
            route("/v1") {
                userController()
            }
        }
    }
}