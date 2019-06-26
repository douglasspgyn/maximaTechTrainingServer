package douglasspgyn.com.github.maximatechtrainingserver.util.mapper

import com.google.gson.Gson

object Mapper {

    private val gson: Gson = Gson()

    fun <T, R> transform(map: T, type: Class<R>): R {
        val json = gson.toJson(map)
        return gson.fromJson(json, type)
    }
}