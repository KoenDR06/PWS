package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

const val SEPARATOR = ";"

@Serializable
data class User(val username: String, val currentMonday: Int? = null, val currentTuesday: Int? = null, val currentWednesday: Int? = null,
                val currentThursday: Int? = null, val currentFriday: Int? = null, val currentSaturday: Int? = null, val currentSunday: Int? = null,
                val nextMonday: Int? = null, val nextTuesday: Int? = null, val nextWednesday: Int? = null, val nextThursday: Int? = null,
                val nextFriday: Int? = null, val nextSaturday: Int? = null, val nextSunday: Int? = null)


//todo: change class name
class UserItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserItem>(UserService.UsersService)

    var username by UserService.UsersService.username
    var diet by UserService.UsersService.diet

    // black magic to convert int array to text (it's just converting to csv (but then with ';'))
    var nextWeeks by UserService.UsersService.nextWeeks.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )

    var allergicTags by UserService.UsersService.allergicTags.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toBoolean() }.toTypedArray() }
    )


}


class UserService(database: Database) {
    object UsersService : IntIdTable() {
        val username = varchar("username", length = 64)

        // Mealplan columns
        val nextWeeks = text("next_weeks")

        // preferences
        val likedTags = text("liked_tags")
        val dislikedTags = text("disliked_tags")
        val allergicTags = text("allergic_tags")
        val dislikedIngredients = text("disliked_ingredients")
        val diet = integer("diet")
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UsersService)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: UserService
        val dietOptions = mapOf(
            "omnivore" to 0,
            "vegetarian" to 1,
            "pescatarian" to 2,
            "vegan" to 3
        )
    }


    suspend fun delete(id: Int) {
        dbQuery {
            UsersService.deleteWhere { UsersService.id.eq(id) }
        }
    }
}
