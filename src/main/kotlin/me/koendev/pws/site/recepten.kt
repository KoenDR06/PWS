package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import me.koendev.pws.data.Recipe
import readInput
import kotlinx.html.*

fun Routing.recepten() {
    val dataLocation = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(dataLocation).joinToString(""))
    get("/recepten") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Recepten"
                }
            }
            body {
                h1 {
                    +"Recepten"
                }
                div {


                    for (i in 0..50) {
                        div {
                            // TODO Make clicking on recipe redirect to recipe page
                            id = recipes[i].id
                            h3 {
                                +recipes[i].title
                            }
                            p {
                                +recipes[i].description
                            }
                        }
                    }


                }
            }
        }
    }

    route("/{recipe_id}") {
        get {
            val recipeId = call.parameters["recipe_id"]
            val recipe = recipes.find { it.id == recipeId }
            if (recipe != null) {
                call.respond(recipe)
            } else {
                throw NotFoundException("Recipe not found")
            }
        }
    }
}