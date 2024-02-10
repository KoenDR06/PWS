package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Routing.registreren() {
    get("/registreren") {
        call.respondHtml(HttpStatusCode.OK) {
            head {

            }
            body {

            }
        }
    }
}