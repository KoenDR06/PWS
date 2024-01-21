package me.koendev.pws.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.api.apiRouting
import me.koendev.pws.site.webRouting

fun Application.configureRouting() {
    install(DoubleReceive)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
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
