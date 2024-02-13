package me.koendev.pws.site

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.RecipeItem
import me.koendev.pws.database.UserItem
import me.koendev.pws.plugins.userService
import me.koendev.pws.site.templates.navBar
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.mealplan() {
    val daysOfWeek = listOf("Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag", "Zondag")
    authenticate("jwt") {
        get("/mealplan") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")
            val userId = userService.readByUsername(username?.asString() ?: "")
            if (userId == null) {
                call.respondText("Er is iets misgegaan...")
            } else {
                val ids = mutableListOf<Int>()
                val locked = mutableListOf<Boolean>()
                for ((index, day) in daysOfWeek.withIndex()) {
                    val state = call.request.queryParameters[day]
                    val response = HttpClient(CIO).post("http://localhost:5000/recommend")
                    val recipeList = response.body<List<Int>>()

                    ids.add(
                        if (state == "on") call.request.queryParameters["$index"]!!.toInt()
                        else recipeList[0]
                    )
                    locked.add(
                        state == "on"
                    )
                }

                if (locked.all { it }) {
                    transaction {
                        val user = UserItem.findById(userId) ?: throw NotFoundException("User not found")
                        user.nextWeeks = user.nextWeeks.sliceArray(0..6) + ids.toTypedArray()
                    }
                }

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +"Genereer uw mealplan" }
                        link(rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                        link(rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                        link(rel = "stylesheet", href = "/static/styles/mealplan.css", type = "text/css")
                        link(rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")

                        script {
                            src = "/static/scripts/change-svg-icon.js"
                        }


                    }
                    body {
                        navBar("mealplan")

                        h1 { +"Mealplan" }

                        form(action = "/mealplan") {
                            div(classes = "recipes-container") {
                                for ((index, recipeId) in ids.withIndex()) {
                                    transaction {
                                        val recipe = RecipeItem[recipeId]
                                        div(classes = "recipe-card") {
                                            a(href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                                                img(src = recipe.imageUrl, alt = "Afbeelding van ${recipe.title}") {
                                                    classes = setOf("recipe-image")
                                                }
                                            }
                                            div(classes = "recipe-info") {
                                                a(href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                                                    h4(classes = "recipe-title") { +daysOfWeek[index] }
                                                    h2(classes = "recipe-title") { +recipe.title }
                                                    p(classes = "recipe-description") { +recipe.description }
                                                }
                                                div(classes = "recipe-stats") {
                                                    span(classes = "recipe-time") { +"${recipe.totalTime} min" }
                                                    label(classes = "lock-button") {
                                                        htmlFor = daysOfWeek[index]

                                                        input(type = InputType.hidden, name = "$index") {
                                                            value = "${ids[index]}"
                                                        }
                                                        input(
                                                            type = InputType.checkBox,
                                                            name = daysOfWeek[index],
                                                            classes = "hidden-checkbox"
                                                        ) {
                                                            id = daysOfWeek[index]
                                                            checked = locked[index]
                                                        }

                                                        img(
                                                            classes = "trash-bin",
                                                            src = if (locked[index]) "/static/images/chefs-hat.svg" else "/static/images/refresh-recipe.svg",
                                                            alt = "Icon"
                                                        ) {
                                                            id = "svg-icon-$index"
                                                            onClick = "changeSvgIcon(${daysOfWeek[index]}, $index)"
                                                        }
                                                    }
                                                }
                                                input(type = InputType.hidden, name = "$index") {
                                                    value = ids[index].toString()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            input(type = InputType.submit) {
                                value = "Genereer nieuwe recepten"
                            }
                        }
                    }
                }
            }
        }
    }
}