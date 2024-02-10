package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

const val SEPARATOR = ";"

@Serializable
data class User(val username: String, val password: String)


class UserItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserItem>(UserService.Users)

    var username by UserService.Users.username
    var password by UserService.Users.password

    var diet by UserService.Users.diet

    // black magic to convert int array to text (it's just converting to csv (but then with ';' ))
    var nextWeeks by UserService.Users.nextWeeks.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )

    var allergicTags by UserService.Users.allergicTags.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toBoolean() }.toTypedArray() }
    )


}


class UserService(database: Database) {
    object Users : IntIdTable() {
        val username = varchar("username", length = 64)
        val password = varchar("password", length = 256)

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
            SchemaUtils.createMissingTablesAndColumns(Users)
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

    suspend fun create(userUsername: String, userPassword: String): Int {
        return dbQuery {
            val newUserId = UserItem.new {
                username = userUsername
                password = userPassword
            }.id.value

            newUserId
        }
    }
}
