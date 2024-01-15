package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class User(val username: String, val currentMonday: Int? = null, val currentTuesday: Int? = null, val currentWednesday: Int? = null,
                val currentThursday: Int? = null, val currentFriday: Int? = null, val currentSaturday: Int? = null, val currentSunday: Int? = null,
                val nextMonday: Int? = null, val nextTuesday: Int? = null, val nextWednesday: Int? = null, val nextThursday: Int? = null,
                val nextFriday: Int? = null, val nextSaturday: Int? = null, val nextSunday: Int? = null)
class UserService(database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val username = varchar("username", length = 50)

        // Mealplan columns
        val currentMonday = integer("current_monday")
        val currentTuesday = integer("current_tuesday")
        val currentWednesday = integer("current_wednesday")
        val currentThursday = integer("current_thursday")
        val currentFriday = integer("current_friday")
        val currentSaturday = integer("current_saturday")
        val currentSunday = integer("current_sunday")
        val nextMonday = integer("next_monday")
        val nextTuesday = integer("next_tuesday")
        val nextWednesday = integer("next_wednesday")
        val nextThursday = integer("next_thursday")
        val nextFriday = integer("next_friday")
        val nextSaturday = integer("next_saturday")
        val nextSunday = integer("next_sunday")


        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: UserService
    }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[username] = user.username
        }[Users.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { User(it[Users.username], it[Users.currentMonday], it[Users.currentTuesday],
                    it[Users.currentWednesday],it[Users.currentThursday], it[Users.currentFriday],
                    it[Users.currentSaturday], it[Users.currentSunday], it[Users.nextMonday], it[Users.nextTuesday],
                    it[Users.nextWednesday], it[Users.nextThursday], it[Users.nextFriday], it[Users.nextSaturday],
                    it[Users.nextSunday]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[username] = user.username
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
