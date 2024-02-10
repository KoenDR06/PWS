package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.RecipeService
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.templates.recipeCard
import org.jetbrains.exposed.sql.select
import println
import kotlin.math.ceil
import kotlin.math.min

fun Routing.zoeken() {
    get("/zoeken") {
        val query = call.request.queryParameters["query"] ?: ""
        val pageNumber = call.request.queryParameters["page"] ?: "1"
        val recipes = recipeService.dbQuery {
            RecipeService.Recipes.select { RecipeService.Recipes.title like "%$query%" }
                .map { it[RecipeService.Recipes.id] }
        }
        recipes.size.println()

        val start = 50 * (pageNumber.toInt() - 1)
        val end = min(start + 49, recipes.size - 1)

        call.respondHtml(HttpStatusCode.OK) {
            head {
                title { +"Zoeken" }
                link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
            }
            body {
                navBar("zoeken")

                form(action = "/zoeken") {
                    input(type = InputType.text, name = "query") {
                        placeholder = "Zoek hier naar een recept:"
                        value = query
                    }
                    input(type = InputType.submit)
                        br {}
                        +"Paginanummer: "
                        input(type = InputType.number, name = "page") {
                            value = pageNumber
                            min = "1"
                            max = ceil(recipes.size.toDouble() / 50.0).toInt().toString()
                        }
                }
                hr {}


                div (classes = "recipes-container") {
                    for (i in recipes.slice(start..end)) {
                        recipeCard(i.value)
                    }
                }
            }
        }
    }
}