package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.templates.recipeCard
import kotlin.random.Random

fun Routing.recepten() {
    get("/recepten") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title { +"Recepten" }
                link (rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")
                link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                script {
                    src = "/static/scripts/like-recipe.js"
                }
            }
            body {
                navBar("recepten")

                h1 { +"Recepten" }
                div (classes = "recipes-container") {
                    val recipeIds = List(50) { Random.nextInt(1, 5044)}
                    for (i in recipeIds) {
                        recipeCard(i)
                    }
                }
            }
        }
    }
}