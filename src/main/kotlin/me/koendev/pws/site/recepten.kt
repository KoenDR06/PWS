package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import me.koendev.pws.plugins.recipeService

fun Routing.recepten() {
    get("/recepten/") {call.respondRedirect("/recepten")}
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
                        runBlocking {
                            val recipe = recipeService.read(i) ?: throw NotFoundException("Recipe was not found in database.")
                            div {
                                style = "display: flex;"
                                id = i.toString()
                                div {
                                    img {
                                        src = recipe.image_url
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
                                    +"${recipe.total_time} minuten om klaar te maken."
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