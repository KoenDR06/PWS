package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.RecipeService
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.plugins.userService
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.templates.recipeCard
import org.jetbrains.exposed.sql.select
import kotlin.math.ceil
import kotlin.math.min

fun Routing.zoeken() {
    authenticate("jwt") {
        get("/zoeken") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")
            val userId = userService.readByUsername(username?.asString() ?: "")
            if (userId == null) {
                call.respondText("Er is iets misgegaan...")
            } else {
                val query = call.request.queryParameters["query"] ?: ""
                val pageNumber = call.request.queryParameters["page"] ?: "1"
                val recipes = recipeService.dbQuery {
                    RecipeService.Recipes.select { RecipeService.Recipes.title like "%$query%" }
                        .map { it[RecipeService.Recipes.id] }
                }

                val start = 50 * (pageNumber.toInt() - 1)
                val end = min(start + 49, recipes.size - 1)

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +"Zoeken" }
                        link(rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                        link(rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                        link(rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")
                        script {
                            src = "/static/scripts/like-recipe.js"
                        }
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


                        div(classes = "recipes-container") {
                            for (i in recipes.slice(start..end)) {
                                recipeCard(i.value, userId)
                            }
                        }
                    }
                }
            }
        }
    }
}