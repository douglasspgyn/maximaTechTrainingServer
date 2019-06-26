package douglasspgyn.com.github.maximatechtrainingserver.repository

import douglasspgyn.com.github.maximatechtrainingserver.model.User
import douglasspgyn.com.github.maximatechtrainingserver.model.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class UserRepository {

    fun findUserById(id: Long): User? {
        var user: User? = null

        transaction {
            Users.select {
                Users.id eq id and Users.deletedAt.isNull()
            }.forEach {
                user = User(it[Users.id].value,
                        it[Users.name],
                        it[Users.email],
                        it[Users.picture],
                        it[Users.password],
                        it[Users.role],
                        it[Users.createdAt],
                        it[Users.updatedAt],
                        it[Users.deletedAt])
            }
        }

        return user
    }

    fun findUserByEmail(email: String): User? {
        var user: User? = null

        transaction {
            Users.select {
                Users.email eq email
            }.forEach {
                user = User(it[Users.id].value,
                        it[Users.name],
                        it[Users.email],
                        it[Users.picture],
                        it[Users.password],
                        it[Users.role],
                        it[Users.createdAt],
                        it[Users.updatedAt],
                        it[Users.deletedAt])
            }
        }

        return user
    }

    fun createUser(user: User): Boolean {
        var id: Long = 0

        transaction {
            id = Users.insertAndGetId {
                it[name] = user.name!!
                it[email] = user.email!!
                it[picture] = user.picture
                it[password] = user.password!!
            }.value
        }

        return id > 0
    }

    fun updateUser(user: User): Boolean {
        return transaction {
            Users.update({ Users.id eq user.id }) { db ->
                user.name?.let {
                    db[name] = it
                }
                user.email?.let {
                    db[email] = it
                }
                user.picture?.let {
                    db[picture] = it
                }
                user.password?.let {
                    db[password] = it
                }
                user.deletedAt?.let {
                    db[deletedAt] = it
                }
            } > 0
        }
    }

    fun deleteUser(user: User): Boolean {
        user.deletedAt = DateTime()
        return updateUser(user)
    }
}