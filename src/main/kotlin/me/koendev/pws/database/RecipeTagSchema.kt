package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable

data class RecipeTag(val recipeId: Int, val tagId: Int)
class RecipeTagsService(database: Database) {
    object RecipeTags : Table() {
        val recipeId = integer("recipe_id").autoIncrement()
        val tagId = integer("tag_id")

        override val primaryKey = PrimaryKey(recipeId, tagId)
    }

    init {
        transaction(database) {
            SchemaUtils.create(RecipeTags)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: RecipeTagsService
    }

    suspend fun create(recipeTag: RecipeTag) = dbQuery {
        RecipeTags.insert {
            it[recipeId] = recipeTag.recipeId
            it[tagId] = recipeTag.tagId

        }
    }

    suspend fun read(id: Int): RecipeTag? {
        return dbQuery {
            RecipeTags.select { RecipeTags.recipeId eq id }
                .map { RecipeTag(it[RecipeTags.recipeId], it[RecipeTags.tagId]) }
                .singleOrNull()
        }
    }
}