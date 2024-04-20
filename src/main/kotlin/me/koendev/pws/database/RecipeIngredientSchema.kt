package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class RecipeIngredient(val recipeId: Int, val ingredientId: Int)
class RecipeIngredientService(database: Database) {
    object RecipeIngredients : Table() {
        val recipeId = integer("recipe_id").autoIncrement()
        val ingredientId = integer("ingredient_id")

        override val primaryKey = PrimaryKey(recipeId, ingredientId)
    }

    init {
        transaction(database) {
            SchemaUtils.create(RecipeIngredients)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: RecipeTagsService
    }

    suspend fun create(recipeIngredient: RecipeIngredient) = dbQuery {
        RecipeIngredients.insert {
            it[recipeId] = recipeIngredient.recipeId
            it[ingredientId] = recipeIngredient.ingredientId

        }
    }

    suspend fun read(id: Int): List<RecipeIngredient> {
        return dbQuery {
            RecipeIngredients.select { RecipeIngredients.recipeId eq id }
                .map { RecipeIngredient(it[RecipeIngredients.recipeId], it[RecipeIngredients.ingredientId]) }
        }
    }
}