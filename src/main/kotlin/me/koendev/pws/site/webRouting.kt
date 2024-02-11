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

    authenticate("auth-jwt") {
        get("/hello") {
            val token = call.request.cookies["JWT_TOKEN"]
            val principal = JWTPrincipal(JWT.decode(token))

            val username = principal.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}