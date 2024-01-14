package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import me.koendev.pws.datatypes.Recipe
import readInput

fun Routing.recepten() {
    val dataLocation = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(dataLocation).joinToString(""))
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
                    for (i in 0..50) {
                        div {
                            onClick = "window.location.href = '/recepten/${recipes[i].id}'"
                            style = "display: flex;"
                            // TODO Make clicking on recipe redirect to recipe page
                            id = recipes[i].id
                            div {
                                img {
                                    src = recipes[i].image_url
                                    height = "100px"
                                }
                            }
                            div {
                                h3 {
                                    +recipes[i].title
                                }
                                p {
                                    +recipes[i].description
                                }
                            }
                        }
                        div {
                            p {
                                +"${recipes[i].total_time} minuten om klaar te maken."
                            }
                        }
                        hr {}
                    }
                }
            }
        }
    }
}