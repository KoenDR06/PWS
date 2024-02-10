package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.currentUserId
import me.koendev.pws.database.RecipeItem
import me.koendev.pws.database.UserItem
import me.koendev.pws.site.templates.navBar
import org.jetbrains.exposed.sql.transactions.transaction
import println
import kotlin.random.Random

fun Routing.mealplan() {
    val daysOfWeek = listOf("Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag", "Zondag")
    get("/mealplan") {

        val ids = mutableListOf<Int>()
        val locked = mutableListOf<Boolean>()
        for ((index, day) in daysOfWeek.withIndex()) {
            val state = call.request.queryParameters[day]
            ids.add(
                if (state == "on") call.request.queryParameters["$index"]!!.toInt()
                else Random.nextInt(1, 5044)
            )
            locked.add(
                if (state == "on") true
                else false
            )
        }

        if(locked.all {it}) {
            transaction {
                val user = UserItem.findById(currentUserId) ?: throw NotFoundException("User not found")
                user.nextWeeks = user.nextWeeks.sliceArray(0..6) + ids.toTypedArray()
            }
        }

        ids.println()

        call.respondHtml(HttpStatusCode.OK) {
            head {
                title { +"Genereer uw mealplan" }
                link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                script {
                    src = "/static/scripts/change-svg-icon.js"
                }
            }
            body {
                navBar("mealplan")

                form(action = "/mealplan") {
                    div (classes = "recipes-container") {
                        for ((index, recipeId) in ids.withIndex()) {
                            transaction {
                                val recipe = RecipeItem[recipeId]
                                div (classes = "recipe-card") {
                                    img (src = recipe.imageUrl, alt = "Afbeelding van ${recipe.title}") {
                                        classes = setOf("recipe-image")
                                    }
                                    div (classes = "recipe-info") {
                                        h4 (classes = "recipe-title") {+daysOfWeek[index]}
                                        h2 (classes = "recipe-title") { +recipe.title }
                                        p (classes = "recipe-description") { +recipe.description }
                                        div (classes = "recipe-stats") {
                                            span (classes = "recipe-time") { +"${recipe.totalTime} min" }
                                            label (classes = "lock-button") {

                                                htmlFor = daysOfWeek[index]

                                                input (type = InputType.checkBox, name = daysOfWeek[index]) {
                                                    id = daysOfWeek[index]
                                                    checked = locked[index]
                                                }
                                                div (classes = "trash-bin") {
                                                    img (classes = "trash-bin", src = "/static/images/trash-bin-closed.svg", alt = "Closed Trash Bin")
                                                }
//                                                div (classes = "trash-bin") {
//                                                    img (classes = "trash-bin", src ="/static/trash-bin-open.svg", alt = "Open Trash Bin") {
//                                                        style="display: none;"
//                                                    }
//                                                }

                                            }
                                        }

                                        input (type = InputType.hidden, name = "$index") {
                                            value = ids[index].toString()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for ((index, day) in daysOfWeek.withIndex()) {
                        input (type = InputType.hidden, name = "$index") {
                            value = "${ids[index]}"
                        }
                        input (type = InputType.checkBox, name = day) {
                            id = day
                            checked = locked[index]
                        }
                        label {
                            htmlFor = day
                            +day
                        }
                        br {}
                    }
                    input(type = InputType.submit)
                }
            }
        }
    }
}