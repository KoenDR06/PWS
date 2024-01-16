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

class RecipeItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RecipeItem>(RecipeService.Recipes)

    var title by RecipeService.Recipes.title
    var description by RecipeService.Recipes.description
    var imageUrl by RecipeService.Recipes.imageUrl
    var prepareTime by RecipeService.Recipes.prepareTime
    var ovenTime by RecipeService.Recipes.ovenTime
    var waitTime by RecipeService.Recipes.waitTime
    var rating by RecipeService.Recipes.rating
    var ratingCount by RecipeService.Recipes.ratingCount

    val totalTime get() = prepareTime + ovenTime + waitTime
}


class RecipeService(database: Database) {
    object Recipes : IntIdTable() {
        val title = varchar("title", length = 128)
        val description = varchar("description", length = 1024)
        val imageUrl = varchar("image_url", length = 128)
        val prepareTime = integer("prepare_time")
        val ovenTime = integer("oven_time")
        val waitTime = integer("wait_time")
        val rating = double("rating")
        val ratingCount = integer("rating_count")
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

    suspend fun delete(id: Int) {
        dbQuery {
            Recipes.deleteWhere { Recipes.id.eq(id) }
        }
    }
}