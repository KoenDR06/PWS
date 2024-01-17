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
import org.jetbrains.exposed.sql.transactions.transaction
import println
import kotlin.random.Random

fun Routing.mealplan() {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
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
                title {
                    +"Genereer uw mealplan"
                }
            }
            body {
                div {
                    for (i in ids) {
                        transaction {
                            val recipe = RecipeItem[i]
                            div {
                                id = recipe.title
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
                form(action = "/mealplan") {
                    for ((index, day) in daysOfWeek.withIndex()) {
                        input(type = InputType.hidden, name = "$index") {
                            value = ids[index].toString()
                        }
                        input(type = InputType.checkBox, name = day) { id = day; checked = locked[index] }
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