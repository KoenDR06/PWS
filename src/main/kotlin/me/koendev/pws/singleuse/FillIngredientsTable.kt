package me.koendev.pws.singleuse

import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.koendev.pws.database.*
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.database
import me.koendev.pws.plugins.recipeIngredientService
import me.koendev.pws.plugins.recipeService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
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
    val recipesAdded = mutableListOf<Int>()

    runBlocking {
        val db = IngredientsService(database = database)
        recipeService = RecipeService(database)
        recipeIngredientService = RecipeIngredientService(database)

        for ((t, recipe) in JSONRecipes.withIndex()) {
            if(recipe.id.substring(3).toInt() !in recipesAdded && t >= 538) {
                recipesAdded.add(recipe.id.substring(3).toInt())

                for (ingredient in recipe.ingredients) {

                    val regex = """(\d*\.?\d+|\d+\.\d*|\d+)(.*)""".toRegex()
                    val matchResult = regex.matchEntire(ingredient["quantity"]!!)

                    val res = if (matchResult != null) {
                        val (numberPart, textPart) = matchResult.destructured
                        Pair(numberPart, textPart)
                    } else {
                        Pair("", ingredient["quantity"]!!)
                    }

                    if (res.first == "") {
                        res.println()
                    }
                    val row = Ingredient(
                        ingredient["ingredient"]!!,
                        if (res.first == "") {
                            null
                        } else {
                            res.first.toDouble()
                        }
                            ,
                        res.second
                    )
                    val newIngredientId = db.create(row)

                    val recipeId = recipeService.dbQuery {
                        RecipeService.Recipes.select { RecipeService.Recipes.title eq recipe.title }
                            .map { it[RecipeService.Recipes.id] }
                            .firstOrNull()
                    } ?: throw NotFoundException("Recipe not found in database")

                    recipeIngredientService.create(RecipeIngredient(recipeId.value, newIngredientId))
                }

                println(t + 1)
            }
        }
    }
}