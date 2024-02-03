package me.koendev.pws.singleuse

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.koendev.pws.database.*
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.database
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.plugins.stepService
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

    val dataLocation = "static/new_recipe_data.json"
    val JSONRecipes = Json.decodeFromString<List<JSONRecipe>>(readInput(dataLocation).joinToString(""))
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