package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import me.koendev.pws.sha256

const val SEPARATOR = ";"

@Serializable
data class User(val username: String, val password: String, val likedRecipes: String = "")


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
    var likedTags by UserService.Users.likedTags.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )
    var dislikedTags by UserService.Users.dislikedTags.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )
    var dislikedIngredients by UserService.Users.dislikedIngredients.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )
    var likedRecipes by UserService.Users.likedRecipes.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toInt() }.toTypedArray() }
    )
}


class UserService(database: Database) {
    object Users : IntIdTable() {
        val username = varchar("username", length = 64)
        val password = varchar("password", length = 256)

        // Mealplan columns
        val nextWeeks = text("next_weeks")

        // Preferences
        val likedTags = text("liked_tags")
        val dislikedTags = text("disliked_tags")
        val allergicTags = text("allergic_tags")
        val dislikedIngredients = text("disliked_ingredients")
        val diet = integer("diet")
        val likedRecipes = text("liked_recipes")
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

    suspend fun create(user: User): Int {
        return dbQuery {
            val newUserId = UserItem.new {
                username = user.username
                password = user.password.sha256()
                nextWeeks = Array(14) { 1 }
                allergicTags = Array(4) { false }
                diet = 0
                likedTags = Array(0) { 1 }
                dislikedTags = Array(0) { 1 }
                dislikedIngredients = Array(0) { 1 }
                likedRecipes = Array(0) { 1 }

            }.id.value

            newUserId
        }
    }

    suspend fun validate(user: User): User? {
        val userToValidate = dbQuery {
            Users.select {
                (Users.username eq user.username) and (Users.password eq user.password.sha256())
            }.map {
                User(it[Users.username], it[Users.password])
            }.singleOrNull()
        }
        return userToValidate
    }

    suspend fun read(user: User): List<User> {
        val users = dbQuery {
            Users.select {
                (Users.username eq user.username) and (Users.password eq user.password.sha256())
            }.map {
                User(it[Users.username], it[Users.password], it[Users.likedRecipes])
            }
        }
        return users
    }

    suspend fun likeRecipe(userId : Int, recipeId: Int): Boolean {
        val likedRecipesColumn = dbQuery {
            Users.select {
                Users.id eq userId
            }.map {
                it[Users.likedRecipes]
            }.singleOrNull()
        } ?: return false

        val userLikedRecipes: MutableList<Int> = mutableListOf()
        if (likedRecipesColumn.split(SEPARATOR)[0] != "") {
            for (recipe in likedRecipesColumn.split(SEPARATOR)) {
                userLikedRecipes.add(recipe.toInt())
            }
        }

        if (recipeId in userLikedRecipes) {
            return false
        } else {
            userLikedRecipes.add(recipeId)
            transaction {
                Users.update({ Users.id eq userId }) {
                    it[likedRecipes] = userLikedRecipes.joinToString(SEPARATOR)
                }
            }
            return true
        }
    }

    suspend fun readByUsername(username: String): Int? {
        val userId = dbQuery {
            Users.select {
                (Users.username eq username)
            }.map {
                it[Users.id]
            }.singleOrNull()
        }
        return userId?.value
    }
}
