package douglasspgyn.com.github.maximatechtrainingserver.service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import douglasspgyn.com.github.maximatechtrainingserver.controller.authentication.response.Token
import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.security.jwt.JwtConfig
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom

class AuthenticationUtil {

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun validatePassword(requestPassword: String, dbPassword: String): Boolean {
        return BCrypt.checkpw(requestPassword, dbPassword)
    }

    private val saltSize: Int = 16

    fun getSaltString(): String {
        val SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val random = SecureRandom()
        while (salt.length < saltSize) {
            val index: Int = (random.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }

        return salt.toString()
    }

    fun generateToken(user: User, clientType: String): Token {
        val accessToken: String = JwtConfig.makeAccessToken(user, clientType)
        val decodedAccessToken: DecodedJWT = JWT.decode(accessToken)
        val refreshToken: String = JwtConfig.makeRefreshToken(user, decodedAccessToken, clientType)
        return Token(JwtConfig.tokenType,
                accessToken,
                refreshToken,
                decodedAccessToken.expiresAt.time,
                decodedAccessToken.id)
    }
}