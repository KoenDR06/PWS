
package me.koendev.pws.singleuse

import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.koendev.pws.database.RecipeService
import me.koendev.pws.database.RecipeTag
import me.koendev.pws.database.RecipeTagsService
import me.koendev.pws.database.TagsService
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.database
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import readInput

fun main() {
    database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        driver = "org.mariadb.jdbc.Driver",
        password = dotEnv["DB_PASSWORD"]
    )

    val recipeLoc = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(recipeLoc).joinToString(""))
    val recipesAdded = mutableListOf<Int>()

    runBlocking {
        val recipeTagDB = RecipeTagsService(database = database)
        val recipeDB = RecipeService(database = database)

        for (recipe in recipes) {
            if(recipe.id.substring(3).toInt() !in recipesAdded) {
                recipesAdded.add(recipe.id.substring(3).toInt())

                val recipeId = recipeDB.dbQuery {
                    TagsService.Tags.select { RecipeService.Recipes.title eq recipe.title }
                        .map { it[RecipeService.Recipes.id] }
                        .singleOrNull()
                } ?: throw NotFoundException("Recipe was not found in database")

                for (tag in recipe.tags) {
                    val tagId = recipeTagDB.dbQuery {
                        TagsService.Tags.select { TagsService.Tags.name eq tag }
                            .map { it[TagsService.Tags.id] }
                            .singleOrNull()
                    } ?: throw NotFoundException("Tag was not found in database. Recipe ID: $recipeId")
                    recipeTagDB.create(RecipeTag(recipeId, tagId = tagId))
                }

            }
        }
    }
}
