package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.TagItem
import org.jetbrains.exposed.sql.transactions.transaction


fun Routing.voorkeuren() {
    get("/voorkeuren") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Voorkeuren aanpassen"
                }
            }
            body {
                div {
                    +"Vul hieronder uw dieet in:"
                    input(type = InputType.radio) {
                        name = "diet"
                        required = true
                        id = "omnivore"
                    }
                    label {
                        htmlFor = "omnivore"
                        +"Omnivoor"
                    }
                    br {
                    }
                    input(type = InputType.radio) {
                        name = "diet"
                        required = true
                        id = "pescetarian"
                    }
                    label {
                        htmlFor = "pescetarian"
                        +"Pescetarier"
                    }
                    br {
                    }
                    input(type = InputType.radio) {
                        name = "diet"
                        required = true
                        id = "vegetarian"
                    }
                    label {
                        htmlFor = "vegetarian"
                        +"Vegetarisch"
                    }
                    br {
                    }
                    input(type = InputType.radio) {
                        name = "diet"
                        required = true
                        id = "vegan"
                    }
                    label {
                        htmlFor = "vegan"
                        +"Vegan"
                    }
                }
                div {
                    p {
                        +"Kies hieronder uw allergieën:"
                    }
                    input(InputType.checkBox) {
                        name = "allergies"
                        required = true
                        id = "gluten"
                    }
                    label {
                        htmlFor = "gluten"
                        +"Gluten"
                    }
                    br {
                    }
                    input(InputType.checkBox) {
                        name = "allergies"
                        required = true
                        id = "lactose"
                    }
                    label {
                        htmlFor = "lactose"
                        +"Lactose"
                    }
                    br {
                    }
                    input(InputType.checkBox) {
                        name = "allergies"
                        required = true
                        id = "nuts"
                    }
                    label {
                        htmlFor = "nuts"
                        +"Noten"
                    }
                    br {
                    }
                    input(InputType.checkBox) {
                        name = "allergies"
                        required = true
                        id = "peanuts"
                    }
                    label {
                        htmlFor = "peanuts"
                        +"Pinda's"
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
                                    required = true
                                    id = "0"
                                }
                                input(type = InputType.radio) {
                                    name = tag.name
                                    required = true
                                    id = "1"
                                }
                                input(type = InputType.radio) {
                                    name = tag.name
                                    required = true
                                    id = "2"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}