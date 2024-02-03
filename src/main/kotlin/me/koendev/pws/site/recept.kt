package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import me.koendev.pws.plugins.ingredientsService
import me.koendev.pws.plugins.recipeIngredientService
import me.koendev.pws.plugins.recipeService
import java.util.*

fun Routing.recept() {
    get("/recepten/{recipe_id}") {
        val recipeId = call.parameters["recipe_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
        val recipe = recipeService.getRecipeById(recipeId)
        if (recipe == null) {
            call.respondHtml(HttpStatusCode.NotFound) {
                head {
                    title {
                        +"Recept niet gevonden."
                    }
                }

                body {
                    p {
                        +"We hebben dit recept niet kunnen vinden."
                    }
                }
            }
        } else {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +recipe.title
                    }
                }

                body {
                    h1 {
                        +recipe.title
                    }
                    img {
                        style = "max-width: 25%;"
                        src = recipe.imageUrl
                    }
                    div {
                        id = "ingredients"

                        h3 {
                            +"Ingrediënten"
                        }

                        runBlocking {
                            val recipeIngredients = recipeIngredientService.read(recipeId)

                            for (recipeIngredient in recipeIngredients) {
                                val ingredient = ingredientsService.read(recipeIngredient.ingredientId) ?:
                                                 throw NotFoundException("Ingredient was not found in database")
                                input(InputType.checkBox) {
                                    name = ingredient.name
                                    id = ingredient.name
                                }
                                label {
                                    htmlFor = ingredient.name
                                    +ingredient.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                }
                                br {}
                            }
                        }
                    }
                    div {
                        id = "steps"
                        h3 {
                            +"Bereiding:"
                        }
//                        for(step in recipe.steps) {
//                            p {
//                                style = "max-width: 25%;"
//                                +step
//                            }
//                        }
                    }

                }
            }
        }
    }
}


