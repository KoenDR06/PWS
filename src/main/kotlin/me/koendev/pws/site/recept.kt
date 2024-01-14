package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import me.koendev.pws.datatypes.Recipe
import readInput

fun Routing.recept() {
    val dataLocation = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(dataLocation).joinToString(""))
    get("/recepten/{recipe_id}/") {
        val recipeId = call.parameters["recipe_id"] ?: throw IllegalArgumentException("Invalid ID")
        call.respondRedirect("/recepten/${recipeId}")
    }
    get("/recepten/{recipe_id}") {
        val recipeId = call.parameters["recipe_id"]
        val recipe = recipes.find { it.id == recipeId }
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
                        src = recipe.image_url
                    }
                    div {
                        id = "ingredients"

                        h3 {
                            +"Ingrediënten"
                        }

                        for(ingredient in recipe.ingredients) {
                            input(type = InputType.checkBox, name = "myCheckbox") {}
                            +"${ingredient["quantity"]!!} ${ingredient["ingredient"]!!}"
                            br {}
                        }
                    }
                    div {
                        id = "steps"
                        h3 {
                            +"Bereiding:"
                        }
                        for(step in recipe.steps) {
                            p {
                                style = "max-width: 25%;"
                                +step
                            }
                        }
                    }

                }
            }
        }
    }
}


