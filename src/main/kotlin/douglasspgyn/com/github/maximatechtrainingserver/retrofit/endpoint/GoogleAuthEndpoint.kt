package douglasspgyn.com.github.maximatechtrainingserver.retrofit.endpoint

import douglasspgyn.com.github.maximatechtrainingserver.retrofit.response.GoogleUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleAuthEndpoint {

    @GET("/oauth2/v3/tokeninfo")
    fun getGoogleUser(@Query("id_token") token: String): Call<GoogleUser>
}