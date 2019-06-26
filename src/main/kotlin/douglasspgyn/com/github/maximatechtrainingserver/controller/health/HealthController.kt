package douglasspgyn.com.github.maximatechtrainingserver.controller.health

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.healthController() {

    get("/health") {
        call.respond("Health")
    }
}