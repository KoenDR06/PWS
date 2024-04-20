package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.plugins.userService
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.templates.recipeCard
import kotlin.random.Random

fun Routing.recepten() {
    authenticate("jwt") {
        get("/recepten") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")
            println(username)
            val userId = userService.readByUsername(username?.asString() ?: "")
            if (userId == null) {
                call.respondText("Er is iets misgegaan...")
                println(username?.asString() ?: "")
            } else {
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +"Recepten" }
                        link(rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")
                        link(rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                        link(rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                        script {
                            src = "/static/scripts/like-recipe.js"
                        }
                    }
                    body {
                        navBar("recepten")

                        h1 {
                            +"Recepten"
                        }
                        div(classes = "recipes-container") {
                            val recipeIds = List(50) { Random.nextInt(1, 2) }
                            for (i in recipeIds) {
                                recipeCard(i, userId)
                            }
                        }
                    }
                }
            }
        }
    }
}