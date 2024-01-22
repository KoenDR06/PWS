package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.RecipeService

fun Routing.recept(recipeService: RecipeService) {
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

//                        for(ingredient in recipe.ingredients) {
//                            input(type = InputType.checkBox) {}
//                            +"${ingredient["quantity"]!!} ${ingredient["ingredient"]!!}"
//                        TODO CHANGE THE PLAIN TEXT TO LABEL WITH HTMLFOR
//                            br {}
//                        }
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


