package douglasspgyn.com.github.maximatechtrainingserver.service.user

import douglasspgyn.com.github.maximatechtrainingserver.controller.user.response.UserProfile
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.repository.UserRepository
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.JWTPayload
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.ROLE_ADMIN
import douglasspgyn.com.github.maximatechtrainingserver.service.authentication.AuthenticationUtil
import douglasspgyn.com.github.maximatechtrainingserver.util.mapper.Mapper
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.BadCredentialsThrowable
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.DatabaseThrowable
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.ForbiddenThrowable
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.NotFoundThrowable
import io.ktor.http.HttpStatusCode

class UserService {

    private val userRepository: UserRepository = UserRepository()
    private val authenticationUtil: AuthenticationUtil = AuthenticationUtil()

    fun getUserById(id: Long, jwtPayload: JWTPayload): UserProfile {
        return if (id == jwtPayload.id || jwtPayload.role == ROLE_ADMIN) {
            val user: User? = userRepository.findUserById(id)
            user?.let {
                Mapper.transform(it, UserProfile::class.java)
            } ?: throw NotFoundThrowable()
        } else {
            throw ForbiddenThrowable()
        }
    }

    fun updateUser(id: Long, user: User, oldPassword: String? = null, jwtPayload: JWTPayload): HttpStatusCode {
        return if (id == jwtPayload.id || jwtPayload.role == ROLE_ADMIN) {
            val userDB = userRepository.findUserById(id)
            userDB?.let { updatedUser ->
                user.name?.let {
                    updatedUser.name = it
                }
                if (oldPassword != null && user.password != null) {
                    if (authenticationUtil.validatePassword(oldPassword, userDB.password!!)) {
                        updatedUser.password = authenticationUtil.hashPassword(user.password!!)
                    } else {
                        throw BadCredentialsThrowable()
                    }
                }
                if (user.email != null && user.password != null) {
                    if (authenticationUtil.validatePassword(user.password!!, userDB.password!!)) {
                        if (userRepository.findUserByEmail(user.email!!) == null) {
                            updatedUser.email = user.email!!
                        } else {
                            throw BadCredentialsThrowable()
                        }
                    } else {
                        throw BadCredentialsThrowable()
                    }
                }
                if (userRepository.updateUser(updatedUser)) {
                    HttpStatusCode.OK
                } else {
                    throw DatabaseThrowable()
                }
            } ?: throw NotFoundThrowable()
        } else {
            throw ForbiddenThrowable()
        }
    }

    fun deleteUser(id: Long, jwtPayload: JWTPayload): HttpStatusCode {
        return if (id == jwtPayload.id || jwtPayload.role == ROLE_ADMIN) {
            val userDB = userRepository.findUserById(id)
            userDB?.let {
                if (userRepository.deleteUser(userDB)) {
                    HttpStatusCode.OK
                } else {
                    throw DatabaseThrowable()
                }
            } ?: throw NotFoundThrowable()
        } else {
            throw ForbiddenThrowable()
        }
    }
}