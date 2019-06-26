package douglasspgyn.com.github.maximatechtrainingserver.model

import org.jetbrains.exposed.dao.LongIdTable
import org.joda.time.DateTime

data class User(var id: Long,
                var name: String?,
                var email: String?,
                var picture: String?,
                var password: String?,
                var role: String?,
                var createdAt: DateTime?,
                var updatedAt: DateTime?,
                var deletedAt: DateTime?)

object Users : LongIdTable("users") {
    val name = varchar("name", length = 100)
    val email = varchar("email", length = 100)
    val picture = varchar("picture", length = 200).nullable()
    val password = varchar("password", length = 100)
    val role = varchar("role", length = 50)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val deletedAt = datetime("deleted_at").nullable()
}