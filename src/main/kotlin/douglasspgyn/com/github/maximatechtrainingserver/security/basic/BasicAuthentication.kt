package douglasspgyn.com.github.maximatechtrainingserver.security.basic

import io.ktor.auth.Authentication

private const val android: String = "android"
private const val basicAndroid: String = "EtUNjEwt9gAY6bef"
private const val ios: String = "ios"
private const val basicIos: String = "ynQiJCqrWYotXI6f"

fun Authentication.Configuration.basicAuthentication(name: String?) {
    basic(name) {
        basicList(hashMapOf(android to basicAndroid, ios to basicIos))
    }
}