package me.koendev.pws.singleuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.koendev.pws.database.RecipeService
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.database
import org.jetbrains.exposed.sql.Database
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
    val recipesAdded = mutableListOf<Int>()

    runBlocking {
        val db = RecipeService(database = database)

        for (recipe in JSONRecipes) {
            if(recipe.id.substring(3).toInt() !in recipesAdded) {
                recipesAdded.add(recipe.id.substring(3).toInt())
                val row = me.koendev.pws.database.Recipe(
                    recipe.title,
                    recipe.description,
                    recipe.image_url,
                    recipe.preparing_time_int,
                    recipe.oven_time_int,
                    recipe.waiting_time_int,
                    recipe.rating,
                    recipe.ratings_count.toInt()
                )
                db.create(row)
            }
        }
    }
}



@Serializable
data class JSONRecipe(val id: String, val title: String, val description: String, val image_url: String,
                      val time_to_prepare: String, val oven_time: String, val waiting_time: String, val tags: List<String>,
                      val rating: Double, val ratings_count: String, val ingredients: List<Map<String, String>>,
                      val steps: List<String>,
//                Custom fields not in data
                      var total_time: Int = 0, var preparing_time_int: Int = 0, var oven_time_int: Int = 0,
                      var waiting_time_int: Int = 0) {

    init {
        preparing_time_int = try {
            time_to_prepare.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }
        oven_time_int = try {
            oven_time.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }
        waiting_time_int = try {
            waiting_time.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }

        total_time = preparing_time_int + oven_time_int + waiting_time_int
    }
}