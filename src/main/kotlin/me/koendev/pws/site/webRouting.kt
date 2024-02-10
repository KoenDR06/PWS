package me.koendev.pws.site

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.webRouting() {
    recepten()
    recept()
    login()
    zoeken()
    mealplan()
    voorkeuren()
    registreren()

    get("/") {
        call.respondRedirect("/recepten")
    }
}