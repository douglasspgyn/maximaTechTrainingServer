package douglasspgyn.com.github.maximatechtrainingserver.controller.authentication

import douglasspgyn.com.github.maximatechtrainingserver.controller.authentication.request.RegisterUser
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.security.basic.BasicPayload
import douglasspgyn.com.github.maximatechtrainingserver.service.authentication.AuthenticationService
import douglasspgyn.com.github.maximatechtrainingserver.util.mapper.Mapper
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.Parameters
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

private val authenticationService: AuthenticationService = AuthenticationService()

fun Route.authenticationController() {

    post("/token") {
        val parameters: Parameters = call.receiveText().parseUrlEncodedParameters()
        call.respond(authenticationService.authenticateCredentials(parameters, call.principal<BasicPayload>()!!.clientType))
    }

    post("/token/{type}") {
        val parameters: Parameters = call.receiveText().parseUrlEncodedParameters()
        call.respond(authenticationService.authenticateSocial(call.parameters["type"]!!, parameters, call.principal<BasicPayload>()!!.clientType))
    }

    post("/register") {
        val registerUser: RegisterUser = call.receive()
        call.respond(authenticationService.registerUser(Mapper.transform(registerUser, User::class.java)))
    }
}