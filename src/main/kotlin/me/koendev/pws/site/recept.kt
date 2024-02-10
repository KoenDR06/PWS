package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import me.koendev.pws.database.IngredientItem
import me.koendev.pws.database.RecipeIngredient
import me.koendev.pws.database.StepItem
import me.koendev.pws.plugins.ingredientsService
import me.koendev.pws.plugins.recipeIngredientService
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.plugins.stepService
import me.koendev.pws.site.templates.navBar
import println
import java.util.*

fun Routing.recept() {
    get("/recepten/{recipe_id}") {
        val recipeId = call.parameters["recipe_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
        val recipe = recipeService.getRecipeById(recipeId)
        if (recipe == null) {
            call.respondHtml(HttpStatusCode.NotFound) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    title {
                        +"Recept niet gevonden."
                    }
                }

                body {
                    navBar()
                    p {
                        +"We hebben dit recept niet kunnen vinden."
                    }
                }
            }
        } else {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    title {
                        +recipe.title
                    }
                }

                body {
                    navBar()
                    h1 {
                        +recipe.title
                    }
                    img {
                        style = "max-width: 512px;"
                        src = recipe.imageUrl
                    }
                    div {
                        id = "ingredients"

                        h3 {
                            +"Ingrediënten"
                        }

                        val recipeIngredients: List<RecipeIngredient>
                        runBlocking {
                            recipeIngredients = recipeIngredientService.read(recipeId)
                        }

                        for (recipeIngredient in recipeIngredients) {
                            val ingredient: IngredientItem
                            runBlocking {
                                ingredient = ingredientsService.read(recipeIngredient.ingredientId)
                                    ?: throw NotFoundException("Ingredient was not found in database")
                            }

                            var amountString = ingredient.amount.toString()
                            if (amountString.endsWith(".0")) {
                                amountString = amountString.slice(0..amountString.length - 3)
                            }

                            input(InputType.checkBox) {
                                name = ingredient.name
                                id = ingredient.name
                            }
                            label {
                                htmlFor = ingredient.name
                                +(amountString + " " + ingredient.unit + " " + ingredient.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                            }
                            br {}
                        }
                    }
                    div {
                        id = "steps"
                        h3 {
                            +"Bereiding"
                        }

                        runBlocking {
                            for (step in stepService.read(recipeId)) {
                                p {
                                    style = "max-width: 512px;"
                                    +step.content
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}


