package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Recipe(val title: String, val description: String, val image_url: String, val prepare_time: Int,
                  val oven_time: Int, val wait_time: Int, val rating: Double, val rating_count: Int,
                  var total_time: Int = 0) {
    init {
        total_time = prepare_time + oven_time + wait_time
    }
}

class RecipeService(private val database: Database) {
    object Recipes : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 128)
        val description = varchar("description", length = 1024)
        val image_url = varchar("image_url", length = 128)
        val prepare_time = integer("prepare_time")
        val oven_time = integer("oven_time")
        val wait_time = integer("wait_time")
        val rating = double("rating")
        val rating_count = integer("rating_count")


        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Recipes)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: RecipeService
    }

    suspend fun create(recipe: Recipe): Int = dbQuery {
        Recipes.insert {
            it[title] = recipe.title
            it[description] = recipe.description
            it[image_url] = recipe.image_url
            it[prepare_time] = recipe.prepare_time
            it[oven_time] = recipe.oven_time
            it[wait_time] = recipe.wait_time
            it[rating] = recipe.rating
            it[rating_count] = recipe.rating_count
        }[Recipes.id]
    }

    suspend fun read(id: Int): Recipe? {
        return dbQuery {
            Recipes.select { Recipes.id eq id }
                .map { Recipe(it[Recipes.title], it[Recipes.description], it[Recipes.image_url],
                    it[Recipes.prepare_time], it[Recipes.oven_time], it[Recipes.wait_time],
                    it[Recipes.rating], it[Recipes.rating_count]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, recipe: Recipe) {
        dbQuery {
            Recipes.update({ Recipes.id eq id }) {
                it[title] = recipe.title
                it[description] = recipe.description
                it[image_url] = recipe.image_url
                it[prepare_time] = recipe.prepare_time
                it[oven_time] = recipe.oven_time
                it[wait_time] = recipe.wait_time
                it[rating] = recipe.rating
                it[rating_count] = recipe.rating_count
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Recipes.deleteWhere { Recipes.id.eq(id) }
        }
    }
}