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
import org.jetbrains.exposed.sql.transactions.transaction
import println
import java.io.File

fun main() {
    database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        driver = "org.mariadb.jdbc.Driver",
        password = dotEnv["DB_PASSWORD"]
    )

    val dataLocation = if(dotEnv["PRODUCTION"] == "True"){
        "root/PWSSite/resources/static/new_recipe_data.json"
    } else {
        "src/main/resources/static/new_recipe_data.json"
    }
    val JSONRecipes = Json.decodeFromString<List<JSONRecipe>>(File("", dataLocation).readLines().joinToString(""))

    val recipes: MutableMap<String, Int> = mutableMapOf()
    var t = 0
    transaction {
        for (recipe in JSONRecipes) {
            for (i in 1..5044) {
                if (recipes[recipe.id] == null) {
                    if (RecipeItem.findById(i)?.title == recipe.title) {
                        recipes[recipe.id] = i
                        println(++t)
                        break
                    }
                }
            }
        }
    }

    val duplicates: MutableList<Int?> = mutableListOf()
    val recipesAdded = mutableListOf<Int>()
    runBlocking {
        val recipeTagDB = RecipeTagsService(database = database)
        t = 0
        for (recipe in JSONRecipes) {
            if(recipe.id.substring(3).toInt() !in recipesAdded) {
                recipesAdded.add(recipe.id.substring(3).toInt())
                for (tag in recipe.tags) {
                    val tagId = recipeTagDB.dbQuery {
                        TagsService.Tags.select { TagsService.Tags.name eq tag }
                            .map { it[TagsService.Tags.id] }
                            .singleOrNull()
                    } ?: throw NotFoundException("Tag was not found in database.")
                    try {
                        recipeTagDB.create(RecipeTag(recipes[recipe.id]!!, tagId = tagId.value))
                    } catch (e: Exception) {
                        println("Recipe with ID ${recipes[recipe.id]} is a duplicate.")
                        duplicates.add(recipes[recipe.id])
                    }
                }

            }
            if (t % 100 == 0) {
                println(t++)
            } else {
                ++t
            }

        }
    }
    duplicates.println()
}
