package douglasspgyn.com.github.maximatechtrainingserver.controller.user.request

import com.google.gson.annotations.SerializedName

data class UpdateUser(val name: String?,
                      val email: String?,
                      val password: String?,
                      @SerializedName("old_password") val oldPassword: String?)