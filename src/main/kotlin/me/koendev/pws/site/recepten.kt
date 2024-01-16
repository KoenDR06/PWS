package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.database.RecipeItem
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.recepten() {
    get("/recepten") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Recepten"
                }
            }
            body {
                h1 {
                    +"Recepten"
                }
                div {
                    for (i in 1..50) {
                        transaction {
                            val recipe = RecipeItem.findById(i)
                            if (recipe == null) {
                                //todo koen you do error stuff here
                                return@transaction
                            } else {
                                div {
                                    style = "display: flex;"
                                    id = i.toString()
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
}