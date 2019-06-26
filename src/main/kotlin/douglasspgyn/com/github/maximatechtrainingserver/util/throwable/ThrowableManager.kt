package douglasspgyn.com.github.maximatechtrainingserver.util.throwable

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun StatusPages.Configuration.throwableManager() {
    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError)
    }

    exception<BadRequestThrowable> {
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<UnauthorizedThrowable> {
        call.respond(HttpStatusCode.Unauthorized)
    }

    exception<ForbiddenThrowable> {
        call.respond(HttpStatusCode.Forbidden)
    }

    exception<NotFoundThrowable> {
        call.respond(HttpStatusCode.NotFound)
    }

    exception<DatabaseThrowable> {
        call.respond(HttpStatusCode.InternalServerError)
    }

    exception<BadCredentialsThrowable> {
        call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
    }
}

class BadRequestThrowable : Throwable()
class UnauthorizedThrowable : Throwable()
class ForbiddenThrowable : Throwable()
class NotFoundThrowable : Throwable()
class DatabaseThrowable : Throwable()
class BadCredentialsThrowable : Throwable()