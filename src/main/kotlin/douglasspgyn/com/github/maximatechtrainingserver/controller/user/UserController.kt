package douglasspgyn.com.github.maximatechtrainingserver.controller.user

import douglasspgyn.com.github.maximatechtrainingserver.controller.user.request.UpdateUser
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.JWTPayload
import douglasspgyn.com.github.maximatechtrainingserver.service.user.UserService
import douglasspgyn.com.github.maximatechtrainingserver.util.mapper.Mapper
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.BadRequestThrowable
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

private val userService: UserService = UserService()

fun Route.userController() {
    route("/users/{id}") {
        get {
            try {
                call.respond(userService.getUserById(call.parameters["id"]!!.toLong(), call.principal()!!))
            } catch (numberFormatException: NumberFormatException) {
                throw BadRequestThrowable()
            }
        }

        put {
            try {
                val updateUser: UpdateUser = call.receive()
                call.respond(userService.updateUser(call.parameters["id"]!!.toLong(), Mapper.transform(updateUser, User::class.java), updateUser.oldPassword, call.principal()!!))
            } catch (numberFormatException: NumberFormatException) {
                throw BadRequestThrowable()
            }
        }

        delete {
            try {
                call.respond(userService.deleteUser(call.parameters["id"]!!.toLong(), call.principal()!!))
            } catch (numberFormatException: NumberFormatException) {
                throw BadRequestThrowable()
            }
        }
    }

    get("/users/profile") {
        call.respond(userService.getUserById(call.principal<JWTPayload>()!!.id, call.principal()!!))
    }
}