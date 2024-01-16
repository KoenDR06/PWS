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

@Serializable
data class Recipe(val title: String, val description: String, val image_url: String, val prepare_time: Int,
                  val oven_time: Int, val wait_time: Int, val rating: Double, val rating_count: Int,
                  var total_time: Int = 0) {
    init {
        total_time = prepare_time + oven_time + wait_time
    }
}

class RecipeService(database: Database) {
    object Recipes : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 128)
        val description = varchar("description", length = 1024)
        val imageUrl = varchar("image_url", length = 128)
        val prepareTime = integer("prepare_time")
        val ovenTime = integer("oven_time")
        val waitTime = integer("wait_time")
        val rating = double("rating")
        val ratingCount = integer("rating_count")


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
            it[imageUrl] = recipe.image_url
            it[prepareTime] = recipe.prepare_time
            it[ovenTime] = recipe.oven_time
            it[waitTime] = recipe.wait_time
            it[rating] = recipe.rating
            it[ratingCount] = recipe.rating_count
        }[Recipes.id]
    }

    suspend fun read(id: Int): Recipe? {
        return dbQuery {
            Recipes.select { Recipes.id eq id }
                .map { Recipe(it[Recipes.title], it[Recipes.description], it[Recipes.imageUrl],
                    it[Recipes.prepareTime], it[Recipes.ovenTime], it[Recipes.waitTime],
                    it[Recipes.rating], it[Recipes.ratingCount]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, recipe: Recipe) {
        dbQuery {
            Recipes.update({ Recipes.id eq id }) {
                it[title] = recipe.title
                it[description] = recipe.description
                it[imageUrl] = recipe.image_url
                it[prepareTime] = recipe.prepare_time
                it[ovenTime] = recipe.oven_time
                it[waitTime] = recipe.wait_time
                it[rating] = recipe.rating
                it[ratingCount] = recipe.rating_count
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Recipes.deleteWhere { Recipes.id.eq(id) }
        }
    }
}