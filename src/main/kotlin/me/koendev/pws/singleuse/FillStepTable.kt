package me.koendev.pws.singleuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.koendev.pws.database.RecipeService
import me.koendev.pws.database.Step
import me.koendev.pws.database.StepService
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.database
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.plugins.stepService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
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
        stepService = StepService(database)
        recipeService = RecipeService(database)

        for ((t, recipe) in JSONRecipes.withIndex()) {
            if(recipe.id.substring(3).toInt() !in recipesAdded && t >= 0) {
                recipesAdded.add(recipe.id.substring(3).toInt())

                val recipeId = recipeService.dbQuery {
                    RecipeService.Recipes.select {
                        RecipeService.Recipes.title eq recipe.title
                    }.map {
                        it[RecipeService.Recipes.id]
                    }.firstOrNull()
                }

                for (step in recipe.steps) {
                    val row = Step(
                        step,
                        recipeId!!.value
                    )
                    stepService.create(row)
                }

                println(t + 1)
            }
        }
    }
}