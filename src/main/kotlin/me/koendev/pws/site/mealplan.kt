package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import me.koendev.pws.database.Recipe
import readInput

fun Routing.mealplan() {
    val dataLocation = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(dataLocation).joinToString(""))

    get("/mealplan/") {
        call.respondRedirect("/mealplan")
    }
    get("/mealplan") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Mealplan"
                }
            }
            body {
                div {
                    id = "recipe-1"
                }
                div {
                    id = "recipe-2"
                }
                div {
                    id = "recipe-3"
                }
                div {
                    id = "recipe-4"
                }
                div {
                    id = "recipe-5"
                }
            }
        }
    }

}