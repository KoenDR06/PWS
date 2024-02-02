// todo fix this
package me.koendev.pws.singleuse

import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.koendev.pws.database.*
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
    val JSONRecipes = Json.decodeFromString<List<JSONRecipe>>(readInput(recipeLoc).joinToString(""))
    val recipesAdded = mutableListOf<Int>()

    runBlocking {
        val recipeTagDB = RecipeTagsService(database = database)

        for (recipe in JSONRecipes) {
            if(recipe.id.substring(3).toInt() !in recipesAdded) {
                recipesAdded.add(recipe.id.substring(3).toInt())


                for (tag in recipe.tags) {
                    val tagId = recipeTagDB.dbQuery {
                        TagsService.Tags.select { TagsService.Tags.name eq tag }
                            .map { it[TagsService.Tags.id] }
                            .singleOrNull()
                    } ?: throw NotFoundException("Tag was not found in database. Recipe ID: ${recipe.id}")
                    recipeTagDB.create(RecipeTag(recipe.id.toInt(), tagId = tagId.value))
                }

            }
        }
    }
}
