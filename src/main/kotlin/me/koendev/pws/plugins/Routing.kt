package me.koendev.pws.plugins

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
import me.koendev.pws.site.webRouting

fun Application.configureRouting() {
    install(DoubleReceive)
    install(Resources)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondHtml(HttpStatusCode.NotFound) {
                head {
                    title {
                        +"Pagina niet gevonden..."
                    }
                }
                body {
                    h1 {
                        +"Helaas hebben we deze pagina niet kunnen vinden."
                    }
                    p {
                        +"Klik hieronder op de knop om naar de homepagina te gaan."
                    }
                    form(action = "/") {
                        input(type = InputType.submit) {
                            value = "Homepagina"
                        }
                    }
                }
            }
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
