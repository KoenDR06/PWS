package me.koendev.pws.plugins

import me.koendev.pws.IAmATeaPot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.api.apiRouting
import me.koendev.pws.site.templates.navBar
import me.koendev.pws.site.webRouting

fun Application.configureRouting() {
    install(DoubleReceive)
    install(Resources)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondHtml(HttpStatusCode.NotFound) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    link (rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")
                    title { +"Pagina niet gevonden..." }
                }
                body {
                    navBar()
                    h1 {
                        +"Helaas hebben we deze pagina niet kunnen vinden."
                    }
                }
            }
        }
        status(HttpStatusCode.IAmATeaPot) { call, _ ->
            call.respondText("I'm a teapot")
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        webRouting()
        apiRouting()

        get(Regex("(?<url>.*)/")) {
            call.respondRedirect(("/" + call.parameters["url"]))
        }

        static("/static") {
            resources("static")
        }
    }
}
