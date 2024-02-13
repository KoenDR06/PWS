package me.koendev.pws.site

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.webRouting() {
    recepten()
    recept()
    zoeken()
    mealplan()
    voorkeuren()

    login()
    registreren()

    get("/") {
        call.respondRedirect("/recepten")
    }
}