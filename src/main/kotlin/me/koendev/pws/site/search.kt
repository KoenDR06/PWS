package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import me.koendev.pws.database.RecipeService
import me.koendev.pws.plugins.recipeService
import org.jetbrains.exposed.sql.select
import kotlin.math.min

fun Routing.search() {
    get("/zoeken") {
        val query = call.request.queryParameters["query"] ?: ""
        val pageNumber = call.request.queryParameters["page"] ?: "1"
        val recipes = recipeService.dbQuery {
            RecipeService.Recipes.select { RecipeService.Recipes.title like "% $query %" }
                .map { it[RecipeService.Recipes.id] }
        }
        val start = 50 * (pageNumber.toInt() - 1)
        val end = min(start + 49, recipes.size - 1)

        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Zoeken"
                }
            }
            body {
                form(action = "/zoeken") {
                    input(type = InputType.text, name = "query") {
                        placeholder = "Zoek hier naar een recept:"
                    }
                    input(type = InputType.submit)
//                        br {}
//                        +"Paginanummer: "
//                        input(type = InputType.number, name = "page") {
//                            value = pageNumber
//                            min = "1"
//                            max = ceil(recipes.size.toDouble() / 50.0).toInt().toString()
//                        }
                }
                hr {}
                for(i in recipes.slice(start..end)) {
                    runBlocking {
                        val recipe = transaction { RecipeItem.findById(i) ?: throw NotFoundException("Recipe was not found in database.") }
                        div {
                            id = i.toString()
                            div {
                                style = "display: flex;"
                                div {
                                    img {
                                        src = recipe.imageUrl
                                        height = "100px"
                                    }
                                }
                                div {
                                    h3 {
                                        +recipe.title
                                    }
                                    p {
                                        +recipe.description
                                    }
                                }
                            }
                            div {
                                style = "display: flex; "
                                p {
                                    +"${recipe.totalTime} minuten om klaar te maken."
                                }
                                a {
                                    style = "margin-top: 16px;" +
                                            "margin-bottom: 16px;" +
                                            "margin-left: 20px;"
                                    href = "/recepten/${i}"
                                    +"Klik hier om meer te lezen"
                                }
                            }
                            hr {}
                        }
                    }
                }
            }
        }
    }
}