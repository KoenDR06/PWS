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
                div("nav-bar") {
                    a(classes = "active left-align") {
                        href = "/"
                        +"Homepagina"
                    }
                    a(classes = "left-align") {
                        href = "/mealplan"
                        +"Mealplan"
                    }
                    a (classes = "left-align"){
                        href = "/voorkeuren"
                        +"Voorkeuren"
                    }


                    a (classes = "right-align") {
                        href = "/login"
                        +"Login"
                    }
                    div (classes = "right-align search-div") {
                        form (action = "/zoeken") {
                            input(type = InputType.text, name = "query", classes = "searchbox") {
                                placeholder = "Zoek hier naar een recept:"
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