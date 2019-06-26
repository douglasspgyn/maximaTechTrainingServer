package douglasspgyn.com.github.maximatechtrainingserver.service.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import douglasspgyn.com.github.maximatechtrainingserver.controller.authentication.response.Token
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.repository.UserRepository
import douglasspgyn.com.github.maximatechtrainingserver.retrofit.Retrofit
import douglasspgyn.com.github.maximatechtrainingserver.retrofit.response.GoogleUser
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.JwtConfig
import douglasspgyn.com.github.maximatechtrainingserver.util.mapper.Mapper
import douglasspgyn.com.github.maximatechtrainingserver.util.throwable.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import retrofit2.Response

class AuthenticationService {

    private val userRepository: UserRepository = UserRepository()
    private val authenticationUtil: AuthenticationUtil = AuthenticationUtil()

    fun authenticateCredentials(parameters: Parameters, clientType: String): Token {
        return when (parameters["grant_type"]) {
            "password" -> {
                authenticateCredentialsPassword(parameters["email"], parameters["password"], clientType)
            }
            "refresh_token" -> {
                authenticateCredentialsRefreshToken(parameters["refresh_token"], clientType)
            }
            else -> throw BadRequestThrowable()
        }
    }

    private fun authenticateCredentialsPassword(email: String?, password: String?, clientType: String): Token {
        return if (email != null && password != null) {
            val userDB: User? = userRepository.findUserByEmail(email)
            when {
                userDB == null -> throw NotFoundThrowable()
                userDB.deletedAt != null -> throw ForbiddenThrowable()
                authenticationUtil.validatePassword(password, userDB.password!!) -> authenticationUtil.generateToken(userDB, clientType)
                else -> throw BadRequestThrowable()
            }
        } else {
            throw BadRequestThrowable()
        }
    }

    private fun authenticateCredentialsRefreshToken(refreshToken: String?, clientType: String): Token {
        return refreshToken?.let {
            try {
                val jwt: DecodedJWT = JwtConfig.refreshTokenVerifier.verify(it)
                val user: User? = userRepository.findUserById(jwt.getClaim("id").asLong())
                user?.let {
                    authenticationUtil.generateToken(user, clientType)
                } ?: throw BadRequestThrowable()
            } catch (ex: JWTVerificationException) {
                throw UnauthorizedThrowable()
            }
        } ?: throw BadRequestThrowable()
    }

    fun authenticateSocial(type: String, parameters: Parameters, clientType: String): Token {
        return when (type) {
            "google" -> {
                authenticateSocialGoogle(parameters["token"], clientType)
            }
            else -> throw BadRequestThrowable()
        }
    }

    private fun authenticateSocialGoogle(token: String?, clientType: String): Token {
        return token?.let { token ->
            val response: Response<GoogleUser> = Retrofit.googleAuthEndpoint.getGoogleUser(token).execute()
            response.body()?.let { googleUser ->
                val user: User = Mapper.transform(googleUser, User::class.java)
                var userDB: User? = userRepository.findUserByEmail(user.email!!)
                if (userDB == null) {
                    user.password = authenticationUtil.getSaltString()
                    doUserRegistration(user)
                    userDB = userRepository.findUserByEmail(user.email!!)
                }

                when {
                    userDB == null -> throw NotFoundThrowable()
                    userDB.deletedAt != null -> throw ForbiddenThrowable()
                    else -> authenticationUtil.generateToken(userDB, clientType)
                }
            } ?: throw UnauthorizedThrowable()
        } ?: throw BadRequestThrowable()
    }

    fun registerUser(registerUser: User): HttpStatusCode {
        return if (registerUser.name != null && registerUser.email != null && registerUser.password != null) {
            val user: User? = userRepository.findUserByEmail(registerUser.email!!)
            when {
                user == null -> doUserRegistration(registerUser)
                user.deletedAt != null -> throw ForbiddenThrowable()
                else -> throw BadRequestThrowable()
            }
        } else {
            throw BadRequestThrowable()
        }
    }

    private fun doUserRegistration(registerUser: User): HttpStatusCode {
        registerUser.password = authenticationUtil.hashPassword(registerUser.password!!)
        return if (userRepository.createUser(registerUser)) {
            HttpStatusCode.Created
        } else {
            throw DatabaseThrowable()
        }
    }
}