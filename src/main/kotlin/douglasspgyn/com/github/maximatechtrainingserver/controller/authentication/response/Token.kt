package douglasspgyn.com.github.maximatechtrainingserver.controller.authentication.response

import com.google.gson.annotations.SerializedName

data class Token(@SerializedName("token_type") val tokenType: String,
                 @SerializedName("access_token") val accessToken: String,
                 @SerializedName("refresh_token") val refreshToken: String,
                 @SerializedName("expires_at") val expiresAt: Long,
                 val jti: String)