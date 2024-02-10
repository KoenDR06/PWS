package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.currentUserId
import me.koendev.pws.database.TagItem
import me.koendev.pws.database.UserItem
import me.koendev.pws.site.templates.navBar
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Routing.voorkeuren() {
    val diets = listOf("omnivore", "pescetarian", "vegetarian", "vegan")
    val allergies = listOf("gluten", "lactose", "nuts", "peanuts")

    get("/voorkeuren") {
        val allergiesInput = mutableListOf<Boolean>()
        for (allergy in allergies) {
            allergiesInput.add(call.request.queryParameters[allergy] != null)
        }

        if(call.request.queryParameters["submitted"] != null) {
            transaction {
                val user = UserItem.findById(currentUserId) ?: throw NotFoundException("User not found")
                user.allergicTags = allergiesInput.toTypedArray()
            }
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    title {
                        +"Uw voorkeuren zijn aangepast!"
                    }
                }
                body {
                    navBar("mealplan")
                    h1 {
                        +"Uw voorkeuren zijn aangepast!"
                    }
                }
            }
        }

        else {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    title {
                        +"Voorkeuren aanpassen"
                    }
                }
                body {
                    navBar("voorkeuren")

                    form("/voorkeuren") {
                        div {
                            +"Vul hieronder uw dieet in:"
                            for(diet in diets) {
                                br {}
                                input(type = InputType.radio, name = diet) {
                                    id = diet
                                }
                                label {
                                    htmlFor = diet.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    +diet
                                }
                            }
                        }
                        div {
                            p {
                                +"Kies hieronder uw allergieën:"
                            }
                            for(allergy in allergies) {
                                input(InputType.checkBox) {
                                    name = allergy
                                    id = allergy
                                }
                                label {
                                    htmlFor = allergy
                                    +allergy.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                }
                                br {}
                            }
                        }
                        div {
                            p {
                                +"Kies hieronder de tags die u vaker wilt zien in uw recommendations:"
                            }
                            transaction {
                                for (tag in TagItem.all()) {
                                    div {
                                        style = "display: flex;"
                                        p {
                                            style = "margin-top: 0; margin-bottom: 0;"
                                            +tag.name
                                        }
                                        input(type = InputType.radio) {
                                            name = tag.name
                                            id = "0"
                                        }
                                        input(type = InputType.radio) {
                                            name = tag.name
                                            id = "1"
                                        }
                                        input(type = InputType.radio) {
                                            name = tag.name
                                            id = "2"
                                        }
                                    }
                                }
                            }
                        }
                        input(type = InputType.submit) {

                        }
                        input(type = InputType.hidden, name = "submitted") {
                            value = "true"
                        }
                    }
                }
            }
        }
    }
}