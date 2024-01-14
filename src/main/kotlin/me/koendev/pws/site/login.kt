package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.title

fun Routing.login() {
    get("/login/") {call.respondRedirect("/login")}
    get("/login") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Inloggen"
                }
            }
            body {

            }
        }
    }
}