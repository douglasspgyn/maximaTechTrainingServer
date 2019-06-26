package douglasspgyn.com.github.maximatechtrainingserver.retrofit

import com.google.gson.GsonBuilder
import douglasspgyn.com.github.maximatechtrainingserver.retrofit.endpoint.GoogleAuthEndpoint
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {

    private const val GOOGLE_AUTH_URL: String = "https://www.googleapis.com"

    private var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(GOOGLE_AUTH_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

    private fun <T> provideService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    var googleAuthEndpoint: GoogleAuthEndpoint = provideService(GoogleAuthEndpoint::class.java)
}