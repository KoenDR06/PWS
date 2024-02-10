package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.plugins.recipeService
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.templates.recipeCard

fun Routing.recepten() {
    get("/recepten") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title { +"Recepten" }
                link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
            }
            body {
                navBar("recepten")

                h1 { +"Recepten" }
                div (classes = "recipes-container") {
                    for (i in 1..50) {
                        recipeCard(i)
                    }
                }
            }
        }
    }
}