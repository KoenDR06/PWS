package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.plugins.recipeService

fun Routing.recepten() {
    get("/recepten") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title { +"Recepten" }
                link (rel = "stylesheet", href = "/static/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/navBar.css", type = "text/css")
            }
            body {
                div (classes = "nav-bar") {
                    ul (classes = "navbar"){
                        li {
                            a {
                                href = "/recepten"
                                +"Recepten"
                            }
                        }
                        li {
                            a {
                                href = "/zoeken"
                                +"Zoeken"
                            }
                        }
                        li {
                            a {
                                href = "/mealplan"
                                +"Mealplan"
                            }
                        }
                        li {
                            a {
                                href = "/voorkeuren"
                                +"Voorkeuren"
                            }
                        }
                    }
                }

                h1 { +"Recepten" }
                div (classes = "recipes-container") {
                    for (i in 1..50) {
                        val recipe = recipeService.getRecipeById(i)
                        if (recipe != null) {
                            a (href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                                div (classes = "recipe-card") {
                                    img (src = recipe.imageUrl, alt = "Afbeelding van ${recipe.title}") {
                                        classes = setOf("recipe-image")
                                    }
                                    div (classes = "recipe-info") {
                                        h2 (classes = "recipe-title") { +recipe.title }
                                        p (classes = "recipe-description") { +recipe.description }
                                        div (classes = "recipe-stats") {
                                            span (classes = "recipe-time") { +"${recipe.totalTime} min" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}