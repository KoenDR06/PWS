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

    private val recipesCache: Map<Int, RecipeItem> = loadRecipesIntoCache(database)

    private fun loadRecipesIntoCache(database: Database): Map<Int, RecipeItem> {
        return transaction(database) {
            RecipeItem.all().associateBy { it.id.value }
        }
    }

    fun getRecipeById(id: Int): RecipeItem? {
        return recipesCache[id]
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

    suspend fun create(recipe: Recipe) {
        dbQuery {
            RecipeItem.new {
                title = recipe.title
                description = recipe.description
                imageUrl = recipe.image_url
                prepareTime = recipe.prepare_time
                ovenTime = recipe.oven_time
                waitTime = recipe.wait_time
                rating = recipe.rating
                ratingCount = recipe.rating_count
            }
        }
    }
}